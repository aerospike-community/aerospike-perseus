package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.aerospike.LuaSetup;
import com.aerospike.perseus.configurations.ResourceFileProvider;
import com.aerospike.perseus.keyCache.Cache;

import java.io.IOException;

public class UDFTest extends Test<Long>{

    public UDFTest(TestCaseConstructorArguments arguments, Cache<Long> cache) throws IOException {
        super(arguments, cache);
        LuaSetup.registerUDF(client, ResourceFileProvider.getUdfPath());
        System.out.println("UDF Code was registered successfully.");
    }

    protected void execute(Long key){
        WritePolicy writePolicy = new WritePolicy(client.writePolicyDefault);
        writePolicy.timeoutDelay = 3000;
        writePolicy.totalTimeout = 9000;
        client.execute(writePolicy, getKey(key), "same_as_expression", "lua_test");
    }

    public String[] getHeader(){
        return "UDF (Lua)\n ".split("\n");
    }
}
