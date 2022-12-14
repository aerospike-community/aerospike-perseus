package com.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.testCases.*;
import com.aerospike.utilities.AutoDiscardingList;
import com.aerospike.utilities.LuaSetup;
import com.aerospike.utilities.StatLogger;
import com.aerospike.utilities.Statable;
import org.luaj.vm2.Lua;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AerospikeClient client = new AerospikeClient(null,
                new Host("172.31.45.155", 3000),
                new Host("172.31.34.13", 3000),
                new Host("172.31.46.191", 3000));
        AerospikeClient client2 = new AerospikeClient(null,
                new Host("172.31.45.155", 3000),
                new Host("172.31.34.13", 3000),
                new Host("172.31.46.191", 3000));

        LuaSetup.registerUDF(client, "lua2", "example.lua");
        var list = new AutoDiscardingList(5000000, .1, .5);


        Test writeTest = new WriteTest(client, "Barclays", "test", 100, list);
        Test readTest = new ReadTest(client2, "Barclays", "test", 50, list);
        Test updateTest = new UpdateTest(client2, "Barclays", "test", 20, list);
        Test expressionReaderTest = new ExpressionReaderTest(client2, "Barclays", "test", 10, list);
        Test expressionWriterTest = new ExpressionWriterTest(client2, "Barclays", "test", 10, list);
        Test searchTest = new SearchTest(client2, "Barclays", "test", 10, list);
        Test udfTest = new UDFTest(client2, "Barclays", "test", 10, list);
        Test udfAggregationTest = new UDFAggregationTest(client2, "Barclays", "test", 10, list);

        ArrayList<Statable> statables = new ArrayList<>();
        statables.add(writeTest);
        statables.add(readTest);
        statables.add(updateTest);
        statables.add(expressionReaderTest);
        statables.add(expressionWriterTest);
        statables.add(udfTest);
        statables.add(searchTest);
        statables.add(udfAggregationTest);
        new StatLogger(statables, 1);

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
