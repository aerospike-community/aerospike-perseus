package com.aerospike.data;

import com.aerospike.client.Bin;

public abstract class Data {
    private final int key;

    protected Data(int key) {
        this.key = key;
    }

    public abstract Bin[] getBins();

    public int getKey() {
        return key;
    }
}
