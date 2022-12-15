package com.aerospike.testCases;

import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;

public class SearchTest extends Test<Integer>{
    private final QueryPolicy policy;
    public SearchTest(AerospikeConnection connection, int numberOfThreads, DataProvider<Integer> provider) {
        super(connection, numberOfThreads, provider);
        policy = new QueryPolicy(connection.getClient().queryPolicyDefault);
        policy.shortQuery = true;
        policy.includeBinData = false;
        connection.getClient().createIndex(null, connection.getNamespace(), connection.getSetName(), "indexOnKeyPlus10", "keyPlus10", IndexType.NUMERIC).waitTillComplete();
    }

    @Override
    protected void execute(Integer key){
        Statement statement = new Statement();
        statement.setFilter(Filter.equal("keyPlus10", key+10));
        statement.setNamespace(connection.getNamespace());
        statement.setSetName(connection.getSetName());
        RecordSet records = connection.getClient().query(policy, statement);
        records.next();
    }

    public String getHeader(){
        return String.format("Searches (%d)", numberOfThreads);
    }
}
