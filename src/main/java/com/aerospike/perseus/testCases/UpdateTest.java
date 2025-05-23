package com.aerospike.perseus.testCases;

import com.aerospike.client.Bin;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.key.KeyCache;

import java.time.Instant;

public class UpdateTest extends Test<Long>{
    private final WritePolicy policy;

    public UpdateTest(TestCaseConstructorArguments arguments, KeyCache cache) {
        super(arguments, cache);
        policy = new WritePolicy();
        policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    }

    protected void execute(Long key){
        Bin updated = new Bin(Record.UPDATE_TIME, Instant.now().toString());
        try{
            client.put(policy, getKey(key), updated);
        } catch(Exception ignored) {
        }
    }
    public String[] getHeader(){
        return "Update\n ".split("\n");
    }

}
