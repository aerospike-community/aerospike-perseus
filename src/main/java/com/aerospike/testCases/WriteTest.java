package com.aerospike.testCases;

import com.aerospike.data.Record;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;
import com.aerospike.data.collector.KeyCollector;

public class WriteTest extends Test<Record>{

    private final KeyCollector<Integer> keyCollector;

    public WriteTest(AerospikeConnection connection, int numberOfThreads, DataProvider<Record> provider, KeyCollector<Integer> keyCollector) {
        super(connection, numberOfThreads, provider);
        this.keyCollector = keyCollector;
    }

    @Override
    protected void execute(Record sales) {
        com.aerospike.client.Key key = getKey(sales.getKey());
        connection.getClient().put(null, key, sales.getBins());
        keyCollector.collect(sales.getKey());

    }

    public String getHeader(){
        return String.format("Writes (%d)", numberOfThreads);
    }
}
