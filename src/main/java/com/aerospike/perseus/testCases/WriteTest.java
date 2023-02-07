package com.aerospike.perseus.testCases;

import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.collector.KeyCollector;
import com.aerospike.perseus.data.provider.DataProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

public class WriteTest extends Test<Record>{
    private final KeyCollector<Integer> keyCollector;

    public WriteTest(AerospikeConfiguration conf, DataProvider<Record> provider, KeyCollector<Integer> keyCollector) {
        super(conf, provider);
        this.keyCollector = keyCollector;
    }

    @Override
    protected void execute(Record record) {
        com.aerospike.client.Key key = getKey(record.getKey());
        client.put(null, key, record.getBins());
        keyCollector.collect(record.getKey());
    }

    public String getHeader(){
        return String.format("Writes (%d)", threadCount.get());
    }
}
