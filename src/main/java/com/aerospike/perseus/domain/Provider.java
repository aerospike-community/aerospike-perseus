package com.aerospike.perseus.domain;

public interface Provider<T> {
    T next();
}
