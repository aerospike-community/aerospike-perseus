package com.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.testCases.LoadTest;
import com.aerospike.testCases.Test;
import com.aerospike.testCases.UpdateTest;
import com.aerospike.utilities.AutoDiscardingList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AerospikeClient client = new AerospikeClient(null, new Host[] {
                new Host("172.31.19.239", 3000),
                new Host("172.31.25.96", 3000)});
        var list = new AutoDiscardingList(500000, 10, 2);
        Test loadTest = new LoadTest(client, "HorseRaceEvents", "test", 85, list);
        Test updateTest = new UpdateTest(client, "HorseRaceEvents", "test", 10, list);

        loadTest.run();
        Thread.sleep(10000);
        updateTest.run();
    }
}
