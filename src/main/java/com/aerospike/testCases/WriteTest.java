package com.aerospike.testCases;

import com.aerospike.data.Data;
import com.aerospike.data.dataGenator.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;
import com.aerospike.data.dataGenator.key.KeyCollector;

public class WriteTest extends Test<Data>{

    private final KeyCollector<Integer> keyCollector;

    public WriteTest(AerospikeConnection connection, int numberOfThreads, DataProvider<Data> provider, KeyCollector<Integer> keyCollector) {
        super(connection, numberOfThreads, provider);
        this.keyCollector = keyCollector;
    }

    @Override
    protected void execute(Data sales) {
        com.aerospike.client.Key key = getKey(sales.getKey());
        connection.getClient().put(null, key, sales.getBins());
        keyCollector.collect(sales.getKey());

    }

    public String getHeader(){
        return String.format("Writes (%d)", numberOfThreads);
    }
}
