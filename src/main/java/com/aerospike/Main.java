package com.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.testCases.*;
import com.aerospike.utilities.AutoDiscardingList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AerospikeClient client = new AerospikeClient(null, new Host("172.31.19.239", 3000),
                new Host("172.31.25.96", 3000));
        AerospikeClient client2 = new AerospikeClient(null, new Host("172.31.19.239", 3000),
                new Host("172.31.25.96", 3000));
        var list = new AutoDiscardingList(500000, 10, 2);

        Test loadTest = new LoadTest(client, "HorseRaceEvents", "test", 90, list);
        loadTest.run();

        Thread.sleep(10000);
        Test updateTest = new UpdateTest(client2, "HorseRaceEvents", "test", 10, list);
        updateTest.run();

        Test searchTest = new SearchTest(client2, "HorseRaceEvents", "test", 1, list);
        searchTest.run();

        Test aggregationTest = new AggregationTest(client2, "HorseRaceEvents", "test", 10, list);
        aggregationTest.run();
    }
}
