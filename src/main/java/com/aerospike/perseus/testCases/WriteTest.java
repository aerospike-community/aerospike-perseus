package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.RecordGenerator;
import com.aerospike.perseus.keyCache.Cache;

public class WriteTest extends Test<Record>{
    private final Cache<Long> cache;

    public WriteTest(TestCaseConstructorArguments arguments, RecordGenerator provider, Cache<Long> cache) {
        super(arguments, provider);
        this.cache = cache;
    }

    @Override
    protected void execute(Record record) {
        com.aerospike.client.Key key = getKey(record.getKey());
        client.put(null, key, record.getBins());
        cache.store(record.getKey());
    }

    public String[] getHeader(){
        return "Write\n ".split("\n");
    }
}
