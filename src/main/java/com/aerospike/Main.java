package com.aerospike;

import com.aerospike.client.Host;
import com.aerospike.data.collector.AutoDiscardingKeyCollector;
import com.aerospike.data.provider.random.SimpleRecordProvider;
import com.aerospike.data.provider.random.TimePeriodProvider;
import com.aerospike.testCases.*;
import com.aerospike.utilities.ThreadAdjuster;
import com.aerospike.utilities.aerospike.AerospikeConnection;
import com.aerospike.utilities.aerospike.LuaSetup;
import com.aerospike.utilities.logger.StatLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LuaSetup.registerUDF(getCon(), "lua2", "example.lua");

        var simpleDataProvider = new SimpleRecordProvider();
        var discardingKeyList = new AutoDiscardingKeyCollector(5000000, .1);
        var timePeriodProvider = new TimePeriodProvider();

        var writeTest = new WriteTest(getCon(), simpleDataProvider, discardingKeyList);
        var readTest = new ReadTest(getCon(), discardingKeyList);
        var updateTest = new UpdateTest(getCon(), discardingKeyList);
        var expressionReaderTest = new ExpressionReaderTest(getCon(), discardingKeyList);
        var expressionWriterTest = new ExpressionWriterTest(getCon(), discardingKeyList);
        var searchTest = new SearchTest(getCon(), discardingKeyList);
        var udfTest = new UDFTest(getCon(), discardingKeyList);
        var udfAggregationTest = new UDFAggregationTest(getCon(), timePeriodProvider);

        Map<String, Test> tests = new HashMap<>();
        tests.put("Write", writeTest);
        tests.put("Read", readTest);
        tests.put("Update", updateTest);
        tests.put("ExpRead", expressionReaderTest);
        tests.put("ExpWrite", expressionWriterTest);
        tests.put("UDF", udfTest);
        tests.put("Search", searchTest);
        tests.put("UDFAgg", udfAggregationTest);

        new StatLogger(new ArrayList(tests.values()), 1);

        new ThreadAdjuster(tests, "./threads.conf");
    }

    private static AerospikeConnection getCon() {
        List<Host> hosts = new ArrayList<>();
        hosts.add(new Host("172.31.45.155", 3000));
        hosts.add(new Host("172.31.34.13", 3000));
        hosts.add(new Host("172.31.46.191", 3000));
        hosts.add(new Host("172.31.40.156", 3000));
        return AerospikeConnection.getConnection(hosts, "Barclays", "test");
    }
}