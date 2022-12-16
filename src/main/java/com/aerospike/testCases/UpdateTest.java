package com.aerospike.testCases;

import com.aerospike.client.Bin;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;

import java.time.Instant;

public class UpdateTest extends Test<Integer>{
    private final WritePolicy policy;

    public UpdateTest(AerospikeConnection connection, DataProvider<Integer> generator) {
        super(connection, generator);
        policy = new WritePolicy();
        policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    }

    protected void execute(Integer key){
        Bin updated = new Bin("UpdateRes", Instant.now().toString());
        try{
            connection.getClient().add(policy, getKey(key), updated);
        } catch(Exception ignored) {
        }
    }

    public String getHeader(){
        return String.format("Updates (%d)", threadCount.get());
    }
}
