package com.aerospike.perseus.domain.key;

public interface KeyCollector<T>{
    void collect(T value);
}
