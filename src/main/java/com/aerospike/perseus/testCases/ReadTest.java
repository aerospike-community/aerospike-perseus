package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.perseus.keyCache.CacheHitAndMissKeyProvider;

public class ReadTest extends Test<Long>{
    private final double hitRatio;
    public ReadTest(AerospikeClient client, CacheHitAndMissKeyProvider provider, String namespace, String setName, double hitRatio) {
        super(client, provider, namespace, setName);
        this.hitRatio = hitRatio;
    }

    @Override
    protected void execute(Long key) {
        client.get(null, getKey(key));
    }

    public String[] getHeader(){
        return String.format("Read\nHitRate %s%d", "%", (int)(hitRatio*100)).split("\n");
    }
}
