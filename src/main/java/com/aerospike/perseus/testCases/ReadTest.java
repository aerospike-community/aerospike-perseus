package com.aerospike.perseus.testCases;

import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class ReadTest extends Test<Integer>{
    private final double hitRatio;
    public ReadTest(AerospikeConfiguration conf, KeyProvider<Integer> provider, double hitRatio) {
        super(conf, provider);
        this.hitRatio = hitRatio;
    }

    @Override
    protected void execute(Integer key) {
        client.get(null, getKey(key));
    }

    public List<String> getHeader(){
        return List.of(String.format("Read(Hit %s%d)", "%", (int)(hitRatio*100)), String.format("%d", threadCount.get()));
    }
}
