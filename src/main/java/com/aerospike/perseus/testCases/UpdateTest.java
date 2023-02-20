package com.aerospike.perseus.testCases;

import com.aerospike.client.Bin;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.data.provider.DataProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.time.Instant;

public class UpdateTest extends Test<Integer>{
    private final WritePolicy policy;

    public UpdateTest(AerospikeConfiguration conf, DataProvider<Integer> generator) {
        super(conf, generator);
        policy = new WritePolicy();
        policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    }

    protected void execute(Integer key){
        Bin updated = new Bin("UpdateRes", Instant.now().toString());
        try{
            client.put(policy, getKey(key), updated);
        } catch(Exception ignored) {
        }
    }

    public String getHeader(){
        return String.format("Updates (%d)", threadCount.get());
    }
}
