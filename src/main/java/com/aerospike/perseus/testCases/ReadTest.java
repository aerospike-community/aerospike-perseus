package com.aerospike.perseus.testCases;

import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class ReadTest extends Test<Long>{
    private final double hitRatio;
    public ReadTest(AerospikeConfiguration conf, KeyProvider<Long> provider, double hitRatio) {
        super(conf, provider);
        this.hitRatio = hitRatio;
    }

    @Override
    protected void execute(Long key) {
        client.get(null, getKey(key));
    }

    public String getHeader(){
        return String.format("Read(Hit %s%d)", "%", (int)(hitRatio*100));
    }
}
