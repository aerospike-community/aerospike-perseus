package com.aerospike.data.dataGenator.key;

public interface KeyCollector<T>{
    void collect(T value);
}
