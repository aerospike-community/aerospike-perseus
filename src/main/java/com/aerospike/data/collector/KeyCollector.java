package com.aerospike.data.collector;

public interface KeyCollector<T>{
    void collect(T value);
}
