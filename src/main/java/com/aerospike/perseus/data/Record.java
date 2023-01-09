package com.aerospike.perseus.data;

import com.aerospike.client.Bin;

public abstract class Record {
    private final int key;

    protected Record(int key) {
        this.key = key;
    }

    public abstract Bin[] getBins();

    public int getKey() {
        return key;
    }
}
