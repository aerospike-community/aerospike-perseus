package com.aerospike.perseus.testCases;

import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.IndexType;
import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class UDFTest extends Test<Long>{

    public UDFTest(AerospikeConfiguration conf, KeyProvider<Long> provider) {
        super(conf, provider);
        client.createIndex(null, conf.getNamespace(), conf.getSetName(), "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(Long key){
        WritePolicy writePolicy = new WritePolicy(client.writePolicyDefault);
        writePolicy.timeoutDelay = 3000;
        writePolicy.totalTimeout = 9000;
        client.execute(writePolicy, getKey(key), "example", "lua_test");
    }

    public String getHeader(){
        return "UDF";
    }
}
