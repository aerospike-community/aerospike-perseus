package com.aerospike.testCases;

import com.aerospike.client.query.IndexType;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;

public class UDFTest extends Test<Integer>{

    public UDFTest(AerospikeConnection connection, int numberOfThreads, DataProvider<Integer> provider) {
        super(connection, numberOfThreads, provider);
        connection.getClient().createIndex(null, connection.getNamespace(), connection.getSetName(), "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(Integer key){
        connection.getClient().execute(null, getKey(key), "example", "lua_test");
    }

    public String getHeader(){
        return String.format("LUAs (%d)", numberOfThreads);
    }
}
