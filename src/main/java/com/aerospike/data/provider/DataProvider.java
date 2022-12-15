package com.aerospike.data.provider;

public interface DataProvider<T> {
    T next();
}
