package com.aerospike.perseus;

import com.aerospike.perseus.aerospike.AerospikeClientProvider;
import com.aerospike.perseus.configurations.TestConfiguration;
import com.aerospike.perseus.configurations.ThreadsProvider;
import com.aerospike.perseus.configurations.pojos.AerospikeConfiguration;
import com.aerospike.perseus.data.generators.*;
import com.aerospike.perseus.keyCache.CacheHitAndMissKeyProvider;
import com.aerospike.perseus.keyCache.CacheStats;
import com.aerospike.perseus.keyCache.KeyCache;
import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.testCases.search.GeospatialSearchTest;
import com.aerospike.perseus.testCases.search.NumericSearchTest;
import com.aerospike.perseus.testCases.search.StringSearchTest;
import com.aerospike.perseus.testCases.LogableTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestSetup {
    private final ThreadsProvider threadsProvider = new ThreadsProvider();
    private final ArrayList<Test> testList = new ArrayList<Test>();
    private final ArrayList<LogableTest> loggableTestList = new ArrayList<LogableTest>();
    private final CacheStats cacheStats;
    private final WriteTest writeTest;

    public TestSetup(AerospikeConfiguration aerospikeConfig, TestConfiguration testConfig) {

        var keyCache = new KeyCache(testConfig.keyCaching.cacheCapacity,testConfig.keyCaching.discardRatio);
        var cacheHitAndMissKeyProvider = new CacheHitAndMissKeyProvider(keyCache, testConfig.readHitRatio);

        var dateGenerator = new DateGenerator();
        var dummyStringGenerator = new DummyStringGenerator(testConfig.recordSize);
        var geoPointGenerator = new GeoPointGenerator();
        var geoJsonGenerator = new GeoJsonGenerator(geoPointGenerator);
        var recordGenerator = new RecordGenerator(dateGenerator, dummyStringGenerator, geoJsonGenerator);
        var batchSimpleRecordsGenerator = new BatchRecordsGenerator(recordGenerator, testConfig.writeBatchSize);
        var batchKeyGenerator = new BatchKeyGenerator(keyCache, testConfig.readBatchSize);
        var timePeriodGenerator = new TimePeriodGenerator();

        var client = AerospikeClientProvider.getClient(aerospikeConfig);

        String namespace = aerospikeConfig.namespace;
        String set = aerospikeConfig.set;

        writeTest = new WriteTest(client, recordGenerator, keyCache, namespace, set);
        testList.add(writeTest);
        testList.add(new ReadTest(client, cacheHitAndMissKeyProvider, namespace, set, testConfig.readHitRatio));
        testList.add(new UpdateTest(client, keyCache, namespace, set));
        testList.add(new ExpressionReadTest(client, keyCache, namespace, set));
        testList.add(new ExpressionWriteTest(client, keyCache, namespace, set));
        testList.add(new BatchWriteTest(client, batchSimpleRecordsGenerator, namespace, set, keyCache, testConfig.writeBatchSize));
        testList.add(new BatchReadTest(client, batchKeyGenerator, namespace, set, testConfig.writeBatchSize));
        if(testConfig.numericIndex) {
            testList.add(new NumericSearchTest(client, keyCache, namespace, set));
        }
        if(testConfig.stringIndex) {
            testList.add(new StringSearchTest(client, keyCache, namespace, set));
        }
        if(testConfig.geoSpatialIndex) {
            testList.add(new GeospatialSearchTest(client, geoPointGenerator, namespace, set));
        }
        try {
            testList.add(new UDFTest(client, keyCache, namespace, set));
        } catch (IOException e) {
            System.out.println("UDF function couldn't be loaded. The UDF test is therefore disabled.");
        }
        if(testConfig.udfAggregation) {
            try {
                testList.add(new UDFAggregationTest(client, timePeriodGenerator, namespace, set));
            } catch (IOException e) {
                System.out.println("UDF Aggregation function couldn't be loaded. The UDF Aggregation test is therefore disabled.");
            }
        }

        loggableTestList.addAll(testList);
        loggableTestList.add(Test.totalTps);
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
        Test.totalTps.getTPS();
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

    public ArrayList<LogableTest> getLoggableTestList() {
        return loggableTestList;
    }

    public CacheStats getCacheStats() {
        return cacheStats;
    }
}
