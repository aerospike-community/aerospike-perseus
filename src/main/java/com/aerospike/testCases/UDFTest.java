package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class UDFTest extends Test{
    private final SampleProvider<Integer> sampleProvider;

    public UDFTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "LUAs");
        this.sampleProvider = sampleProvider;
        Policy policy = new Policy(client.queryPolicyDefault);
        policy.setTimeout(120000);
        client.createIndex(null, namespace, setName, "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void loop(){
        Stream.generate(sampleProvider::getSample)
                .forEach(key -> find(new Key(namespace, setName, key)));
    }

    private void find(Key key){
        client.execute(null, key, "example", "lua_test");
        counter.getAndIncrement();
    }
}
