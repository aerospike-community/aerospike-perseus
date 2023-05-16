package com.aerospike.perseus.testCases;

import com.aerospike.client.Bin;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.time.Instant;
import java.util.List;

public class UpdateTest extends Test<Long>{
    private final WritePolicy policy;

    public UpdateTest(AerospikeConfiguration conf, KeyProvider<Long> provider) {
        super(conf, provider);
        policy = new WritePolicy();
        policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    }

    protected void execute(Long key){
        Bin updated = new Bin("UpdateRes", Instant.now().toString());
        try{
            client.put(policy, getKey(key), updated);
        } catch(Exception ignored) {
        }
    }
    public String getHeader(){
        return "Update";
    }

}
