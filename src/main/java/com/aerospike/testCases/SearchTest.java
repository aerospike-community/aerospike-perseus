package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.utilities.RandomSampleProvider;

import java.util.stream.Stream;

public class SearchTest extends Test{
    private final RandomSampleProvider<Integer> randomSampleProvider;
    private final QueryPolicy policy;
    public SearchTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, RandomSampleProvider<Integer> randomSampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Searches");
        this.randomSampleProvider = randomSampleProvider;
        policy = new QueryPolicy(client.queryPolicyDefault);
        policy.shortQuery = true;
        policy.includeBinData = false;
        client.createIndex(null, namespace, setName, "indexOnKeyPlus10", "keyPlus10", IndexType.NUMERIC).waitTillComplete();
    }

    protected void loop(){
        Stream.generate(randomSampleProvider::getRandomSample)
                .forEach(this::find);
    }

    private void find(int sample){
        Statement statement = new Statement();
        statement.setFilter(Filter.equal("keyPlus10", sample+10));
        statement.setNamespace(namespace);
        statement.setSetName(setName);
        RecordSet records = client.query(policy, statement);
        records.next();
        counter.getAndIncrement();
    }
}
