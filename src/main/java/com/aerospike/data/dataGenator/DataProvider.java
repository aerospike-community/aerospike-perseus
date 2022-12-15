package com.aerospike.data.dataGenator;

public interface DataProvider<T> {
    T next();
}
