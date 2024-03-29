package com.aerospike.perseus.testCases;

import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class SearchTest extends Test<Long>{
    private final QueryPolicy policy;
    public SearchTest(AerospikeConfiguration conf, KeyProvider<Long> provider) {
        super(conf, provider);
        policy = new QueryPolicy(client.queryPolicyDefault);
        policy.shortQuery = true;
        policy.includeBinData = false;
        client.createIndex(null, conf.getNamespace(), conf.getSetName(), "indexOnKeyPlus10", "keyPlus10", IndexType.NUMERIC).waitTillComplete();
    }

    @Override
    protected void execute(Long key){
        Statement statement = new Statement();
        statement.setFilter(Filter.equal("keyPlus10", key+10));
        statement.setNamespace(conf.getNamespace());
        statement.setSetName(conf.getSetName());
        RecordSet records = client.query(policy, statement);
        records.next();
        records.close();
    }

    public String getHeader(){
        return "Search";
    }
}
