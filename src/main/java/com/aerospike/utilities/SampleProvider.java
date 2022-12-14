package com.aerospike.utilities;

public interface SampleProvider <T>{
    T getRandomSample();
    T getSample();
}
