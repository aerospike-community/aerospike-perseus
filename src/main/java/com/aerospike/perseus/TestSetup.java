package com.aerospike.perseus;

import com.aerospike.perseus.aerospike.AerospikeClientProvider;
import com.aerospike.perseus.configurations.TestConfiguration;
import com.aerospike.perseus.configurations.ThreadsProvider;
import com.aerospike.perseus.configurations.pojos.AerospikeConfiguration;
import com.aerospike.perseus.data.generators.*;
import com.aerospike.perseus.keyCache.CacheHitAndMissKeyProvider;
import com.aerospike.perseus.presentation.CacheStats;
import com.aerospike.perseus.keyCache.KeyCache;
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
    private final CacheStats cacheStats;
    private final WriteTest writeTest;
    private final TotalTpsCounter totalTpsCounter;

    public TestSetup(AerospikeConfiguration aerospikeConfig, TestConfiguration testConfig) throws InterruptedException {

        var keyCache = new KeyCache(testConfig.keyCaching.cacheCapacity,testConfig.keyCaching.discardRatio);
        var cacheHitAndMissKeyProvider = new CacheHitAndMissKeyProvider(keyCache, testConfig.readHitRatio);

        var dateGenerator = new DateGenerator();
        var timePeriodGenerator = new TimePeriodGenerator(dateGenerator, testConfig.rangeQueryConfiguration);
        var dummyStringGenerator = new DummyBlobGenerator(testConfig.recordSize);
        var geoPointGenerator = new GeoPointGenerator();
        var geoJsonGenerator = new GeoJsonGenerator(geoPointGenerator);
        var recordGenerator = new RecordGenerator(dateGenerator, dummyStringGenerator, geoJsonGenerator);
        var batchSimpleRecordsGenerator = new BatchRecordsGenerator(recordGenerator, testConfig.writeBatchSize);
        var batchKeyGenerator = new BatchKeyGenerator(keyCache, testConfig.readBatchSize);
        totalTpsCounter = new TotalTpsCounter();
        var client = AerospikeClientProvider.getClient(aerospikeConfig);

        var arguments = new TestCaseConstructorArguments(client, aerospikeConfig.namespace, aerospikeConfig.set, totalTpsCounter);

        writeTest = new WriteTest(arguments, recordGenerator, keyCache);
        testList.add(writeTest);
        testList.add(new ReadTest(arguments, cacheHitAndMissKeyProvider, testConfig.readHitRatio));
        testList.add(new UpdateTest(arguments, keyCache));
        testList.add(new DeleteTest(arguments, keyCache));
        testList.add(new ExpressionReadTest(arguments, keyCache));
        testList.add(new ExpressionWriteTest(arguments, keyCache));
        testList.add(new BatchWriteTest(arguments, batchSimpleRecordsGenerator, keyCache, testConfig.writeBatchSize));
        testList.add(new BatchReadTest(arguments, batchKeyGenerator, testConfig.readBatchSize));
        if(testConfig.numericIndex) {
            testList.add(new NumericSearchTest(arguments, keyCache));
        }
        if(testConfig.stringIndex) {
            testList.add(new StringSearchTest(arguments, keyCache));
        }
        if(testConfig.geoSpatialIndex) {
            testList.add(new GeospatialSearchTest(arguments, geoPointGenerator));
        }
        try {
            testList.add(new UDFTest(arguments, keyCache));
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
        cacheStats = keyCache;
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

    public CacheStats getCacheStats() {
        return cacheStats;
    }

    public TotalTpsCounter getTotalTps() {
        return totalTpsCounter;
    }
}
