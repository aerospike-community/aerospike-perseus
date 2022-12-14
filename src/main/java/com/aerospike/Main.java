package com.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.testCases.*;
import com.aerospike.utilities.AutoDiscardingList;

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
        var list = new AutoDiscardingList(500000, 10, 2);

        Test writeTest = new WriteTest(client, "Barclays", "test", 100, list);
        Test readTest = new ReadTest(client2, "Barclays", "test", 50, list);
        Test updateTest = new UpdateTest(client2, "Barclays", "test", 20, list);
        Test expressionReaderTest = new ExpressionReaderTest(client2, "Barclays", "test", 10, list);
        Test expressionWriterTest = new ExpressionWriterTest(client2, "Barclays", "test", 10, list);
        Test searchTest = new SearchTest(client2, "Barclays", "test", 10, list);
        Test udfTest = new UDFTest(client2, "Barclays", "test", 10, list);
        Test udfAggregationTest = new UDFAggregationTest(client2, "Barclays", "test", 10, list);


        writeTest.run();

        Thread.sleep(30000);
        readTest.run();

        Thread.sleep(30000);
        updateTest.run();

        Thread.sleep(30000);
        expressionReaderTest.run();

        Thread.sleep(30000);
        expressionWriterTest.run();

        Thread.sleep(30000);
        searchTest.run();

        Thread.sleep(30000);
        udfTest.run();
//
//        Thread.sleep(30000);
//        udfAggregationTest.run();
    }
}
