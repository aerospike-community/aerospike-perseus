package com.aerospike.perseus.data.collector;

public interface KeyCollector<T>{
    void collect(T value);
}
