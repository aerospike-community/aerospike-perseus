package com.aerospike.perseus.testCases.search;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.presentation.TotalTpsCounter;
import com.aerospike.perseus.testCases.Test;
import com.aerospike.perseus.testCases.TestCaseConstructorArguments;

import java.util.Iterator;

public abstract class BaseSearchTest<T> extends Test<T> {
    private final QueryPolicy policy;
    public BaseSearchTest(TestCaseConstructorArguments arguments, Iterator<T> provider) {
        super(arguments, provider);
        policy = new QueryPolicy(client.queryPolicyDefault);
        policy.shortQuery = true;
        policy.includeBinData = false;
    }

    @Override
    protected void execute(T t){
        Statement statement = getStatement(t);
        RecordSet records = client.query(policy, statement);
        records.next();
        records.close();
    }

    protected abstract Statement getStatement(T t);

    public abstract String[] getHeader();
}
