package com.aerospike.perseus;

import com.aerospike.perseus.aerospike.AerospikeClientProvider;
import com.aerospike.perseus.configurations.TestConfiguration;
import com.aerospike.perseus.configurations.ThreadsProvider;
import com.aerospike.perseus.configurations.pojos.AerospikeConfiguration;
import com.aerospike.perseus.data.generators.*;
import com.aerospike.perseus.data.generators.key.BatchedFromKeyCacheGenerator;
import com.aerospike.perseus.data.generators.key.ProbabilisticKeyCache;
import com.aerospike.perseus.data.generators.key.KeyGenerator;
import com.aerospike.perseus.presentation.TotalTpsCounter;
import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.testCases.search.GeospatialSearchTest;
import com.aerospike.perseus.testCases.search.NumericSearchTest;
import com.aerospike.perseus.testCases.search.StringSearchTest;
import com.aerospike.perseus.presentation.TPSLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestSetup {
    private final ThreadsProvider threadsProvider = new ThreadsProvider();
    private final ArrayList<Test> testList = new ArrayList<Test>();
    private final WriteTest writeTest;
    private final TotalTpsCounter totalTpsCounter;

    public TestSetup(AerospikeConfiguration aerospikeConfig, TestConfiguration testConfig) throws InterruptedException {

        var client = AerospikeClientProvider.getClient(aerospikeConfig);
        var keyProvider = new KeyGenerator(client, testConfig.perseusId, aerospikeConfig.namespace);
        var cachedKeyProvider = keyProvider.getCache();
        var probabilisticKeyCache = new ProbabilisticKeyCache(cachedKeyProvider, testConfig.readHitRatio);
        var dateGenerator = new DateGenerator();
        var timePeriodGenerator = new TimePeriodGenerator(dateGenerator, testConfig.rangeQueryConfiguration);
        var dummyStringGenerator = new DummyBlobGenerator(testConfig.recordSize);
        var geoPointGenerator = new GeoPointGenerator();
        var geoJsonGenerator = new GeoJsonGenerator(geoPointGenerator);
        var recordGenerator = new RecordGenerator(dateGenerator, dummyStringGenerator, geoJsonGenerator, keyProvider);
        var batchSimpleRecordsGenerator = new BatchRecordsGenerator(recordGenerator, testConfig.writeBatchSize);
        var batchCachedKeyGenerator = new BatchedFromKeyCacheGenerator(keyProvider.getCache(), testConfig.readBatchSize);
        totalTpsCounter = new TotalTpsCounter();

        var arguments = new TestCaseConstructorArguments(client, aerospikeConfig.namespace, aerospikeConfig.set, totalTpsCounter);

        writeTest = new WriteTest(arguments, recordGenerator);
        testList.add(writeTest);
        testList.add(new ReadTest(arguments, probabilisticKeyCache, testConfig.readHitRatio));
        testList.add(new UpdateTest(arguments, cachedKeyProvider));
        testList.add(new DeleteTest(arguments, cachedKeyProvider));
        testList.add(new ExpressionReadTest(arguments, cachedKeyProvider));
        testList.add(new ExpressionWriteTest(arguments, cachedKeyProvider));
        testList.add(new BatchWriteTest(arguments, batchSimpleRecordsGenerator, testConfig.writeBatchSize));
        testList.add(new BatchReadTest(arguments, batchCachedKeyGenerator, testConfig.readBatchSize));
        if(testConfig.numericIndex) {
            testList.add(new NumericSearchTest(arguments, cachedKeyProvider));
        }
        if(testConfig.stringIndex) {
            testList.add(new StringSearchTest(arguments, cachedKeyProvider));
        }
        if(testConfig.geoSpatialIndex) {
            testList.add(new GeospatialSearchTest(arguments, geoPointGenerator));
        }
        try {
            testList.add(new UDFTest(arguments, cachedKeyProvider));
        } catch (IOException e) {
            System.out.println("UDF function couldn't be loaded. The UDF test is therefore disabled.");
        }
        if(testConfig.udfAggregation) {
            try {
                testList.add(new UDFAggregationTest(arguments, timePeriodGenerator));
            } catch (IOException e) {
                System.out.println("UDF Aggregation function couldn't be loaded. The UDF Aggregation test is therefore disabled.");
            }
        }
        if(testConfig.rangeQuery) {
            try {
                testList.add(new RangeQueryTest(arguments, timePeriodGenerator));
            } catch (IOException e) {
                System.out.println("UDF Aggregation function couldn't be loaded. The UDF Aggregation test is therefore disabled.");
            }
        }
    }

    public void startTest() {
        warmUp();

        var scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                this::setThread, 0, 1, TimeUnit.SECONDS);
    }

    private void warmUp() {
        writeTest.setThreads(5);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        writeTest.getTPS();
        totalTpsCounter.getTPS();
    }

    private void setThread() {
        var threads = threadsProvider.getThreads();
        var map = testList.stream().collect(Collectors.toMap(
                testCase -> testCase.getClass().getSimpleName().replace("Test", "").toLowerCase(),
                testCase-> testCase));
        for (String thread: threads.keySet()) {
            if(map.containsKey(thread.toLowerCase()))
                map.get(thread.toLowerCase()).setThreads(threads.get(thread));
        }
    }

    public List<TPSLogger> getLoggableTestList() {
        return testList.stream().map(test -> (TPSLogger)test).toList();
    }

    public TotalTpsCounter getTotalTps() {
        return totalTpsCounter;
    }
}
