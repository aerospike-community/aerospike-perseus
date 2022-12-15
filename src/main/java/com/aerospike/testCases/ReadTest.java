package com.aerospike.testCases;

import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;

public class ReadTest extends Test<Integer>{
    public ReadTest(AerospikeConnection connection, int numberOfThreads, DataProvider<Integer> provider) {
        super(connection, numberOfThreads, provider);
    }

    @Override
    protected void execute(Integer key) {
        connection.getClient().get(null, getKey(key));
    }

    public String getHeader(){
        return String.format( "Reads (%d)", numberOfThreads);
    }
}
