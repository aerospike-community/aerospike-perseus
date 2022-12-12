package com.aerospike.utilities;

public interface SampleCollector <T>{
    void collectSample(T value);
}
