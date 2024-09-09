package com.aerospike.perseus.testCases;

import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.keyCache.Cache;

public class DeleteTest extends Test<Long>{
    private final WritePolicy deletePolicy;
    public DeleteTest(TestCaseConstructorArguments arguments, Cache<Long> cache) {
        super(arguments, cache);
        deletePolicy = new WritePolicy();
        deletePolicy.durableDelete = true;

    }

    @Override
    protected void execute(Long key) {
        client.delete(deletePolicy, getKey(key));
    }

    public String[] getHeader(){
        return "Delete\n ".split("\n");
    }
}
