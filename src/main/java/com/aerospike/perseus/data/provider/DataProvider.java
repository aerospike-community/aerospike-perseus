package com.aerospike.perseus.data.provider;

public interface DataProvider<T> {
    T next();
}
