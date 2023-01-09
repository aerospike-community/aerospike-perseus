package com.aerospike.perseus.testCases;

import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.IndexType;
import com.aerospike.perseus.data.provider.DataProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

public class UDFTest extends Test<Integer>{

    public UDFTest(AerospikeConfiguration conf, DataProvider<Integer> provider) {
        super(conf, provider);
        client.createIndex(null, conf.getNamespace(), conf.getSetName(), "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(Integer key){
        WritePolicy writePolicy = new WritePolicy(client.writePolicyDefault);
        writePolicy.timeoutDelay = 3000;
        writePolicy.totalTimeout = 9000;
        client.execute(writePolicy, getKey(key), "example", "lua_test");
    }

    public String getHeader(){
        return String.format("LUAs (%d)", threadCount.get());
    }
}
