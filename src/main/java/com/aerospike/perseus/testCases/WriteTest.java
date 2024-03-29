package com.aerospike.perseus.testCases;

import com.aerospike.perseus.domain.data.DataProvider;
import com.aerospike.perseus.domain.data.Record;
import com.aerospike.perseus.domain.key.KeyCollector;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class WriteTest extends Test<Record>{
    private final KeyCollector<Long> keyCollector;

    public WriteTest(AerospikeConfiguration conf, DataProvider<Record> provider, KeyCollector<Long> keyCollector) {
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
        return "Write";
    }
}
