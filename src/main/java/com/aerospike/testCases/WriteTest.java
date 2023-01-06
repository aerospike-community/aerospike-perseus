package com.aerospike.testCases;

import com.aerospike.data.Record;
import com.aerospike.data.collector.KeyCollector;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConfiguration;

public class WriteTest extends Test<Record>{

    private final KeyCollector<Integer> keyCollector;

    public WriteTest(AerospikeConfiguration conf, DataProvider<Record> provider, KeyCollector<Integer> keyCollector) {
        super(conf, provider);
        this.keyCollector = keyCollector;
    }

    @Override
    protected void execute(Record sales) {
        com.aerospike.client.Key key = getKey(sales.getKey());
        client.put(null, key, sales.getBins());
        keyCollector.collect(sales.getKey());
    }

    public String getHeader(){
        return String.format("Writes (%d)", threadCount.get());
    }
}
