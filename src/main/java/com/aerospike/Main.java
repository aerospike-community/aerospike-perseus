package com.aerospike;

import com.aerospike.client.Host;
import com.aerospike.data.dataGenator.TimePeriodProvider;
import com.aerospike.data.dataGenator.random.SimpleDataProvider;
import com.aerospike.testCases.*;
import com.aerospike.utilities.aerospike.AerospikeConnection;
import com.aerospike.utilities.aerospike.LuaSetup;
import com.aerospike.data.dataGenator.key.AutoDiscardingKeyCollector;
import com.aerospike.utilities.logger.StatLogger;
import com.aerospike.utilities.logger.Logable;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Host> hosts = new ArrayList<>();
        hosts.add(new Host("172.31.45.155", 3000));
        hosts.add(new Host("172.31.34.13", 3000));
        hosts.add(new Host("172.31.46.191", 3000));
        hosts.add(new Host("172.31.40.156", 3000));
        var con1 = AerospikeConnection.getConnection(hosts, "Barclays", "test" );
        var con2 = AerospikeConnection.getConnection(hosts, "Barclays", "test" );

        LuaSetup.registerUDF(con1, "lua2", "example.lua");

        var simpleDataProvider = new SimpleDataProvider();
        var discardingKeyList = new AutoDiscardingKeyCollector(5000000, .1);
        var timePeriodProvider = new TimePeriodProvider();


        var writeTest = new WriteTest(con1,100, simpleDataProvider, discardingKeyList);
        var readTest = new ReadTest(con2, 50, discardingKeyList);
        var updateTest = new UpdateTest(con2, 20, discardingKeyList);
        var expressionReaderTest = new ExpressionReaderTest(con2, 10, discardingKeyList);
        var expressionWriterTest = new ExpressionWriterTest(con2,  10, discardingKeyList);
        var searchTest = new SearchTest(con2,  10, discardingKeyList);
        var udfTest = new UDFTest(con2,  10, discardingKeyList);
        var udfAggregationTest = new UDFAggregationTest(con2, 10, timePeriodProvider);

        ArrayList<Logable> logables = new ArrayList<>();
        logables.add(writeTest);
        logables.add(readTest);
        logables.add(updateTest);
        logables.add(expressionReaderTest);
        logables.add(expressionWriterTest);
        logables.add(udfTest);
        logables.add(searchTest);
        logables.add(udfAggregationTest);
        new StatLogger(logables, 1);

        writeTest.run();

        Thread.sleep(20000);
        readTest.run();

        Thread.sleep(20000);
        updateTest.run();

        Thread.sleep(20000);
        expressionReaderTest.run();

        Thread.sleep(20000);
        expressionWriterTest.run();

        Thread.sleep(20000);
        udfTest.run();

        Thread.sleep(20000);
        searchTest.run();
//
//        Thread.sleep(30000);
//        udfAggregationTest.run();
    }
}
