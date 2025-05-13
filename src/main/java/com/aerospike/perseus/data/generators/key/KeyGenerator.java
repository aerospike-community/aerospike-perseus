package com.aerospike.perseus.data.generators.key;

import com.aerospike.client.AerospikeClient;
import com.aerospike.perseus.data.generators.BaseGenerator;

public class KeyGenerator extends BaseGenerator<Long> {
    protected KeyCache keyCache;

    public KeyGenerator(AerospikeClient client, int perseusId, String namespace) {
        keyCache = new KeyCache(client, String.valueOf(perseusId), ((long)perseusId)*10_000_000_000_000L, namespace);
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Long next(){
        return keyCache.getCurrent().getAndIncrement();
    }

    public KeyCache getCache() {
        return keyCache;
    }
}
