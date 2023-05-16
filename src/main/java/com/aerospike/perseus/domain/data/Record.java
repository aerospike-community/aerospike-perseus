package com.aerospike.perseus.domain.data;

import com.aerospike.client.Bin;

import java.util.Map;

public abstract class Record {
    private final long key;

    protected Record(long key) {
        this.key = key;
    }

    public abstract Bin[] getBins();

    public long getKey() {
        return key;
    }
}
