package com.aerospike.random;

import com.aerospike.raw.Data;

public interface DataGenerator {
    Data next();
}
