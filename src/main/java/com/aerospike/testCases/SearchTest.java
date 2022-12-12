package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class SearchTest extends Test{
    private final SampleProvider<Integer> sampleProvider;

    public SearchTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Searches", 1);
        this.sampleProvider = sampleProvider;
        client.createIndex(null, namespace, setName, "indexOnKeyPlus10", "keyPlus10", IndexType.NUMERIC).waitTillComplete();
    }

    protected void loop(){
        Stream.generate(sampleProvider::getSample)
                .forEach(this::find);
    }

    private void find(int sample){
        Statement statement = new Statement();
        statement.setFilter(Filter.equal("keyPlus10", sample+10));

        statement.setNamespace(namespace);
        statement.setSetName(setName);

        RecordSet records = client.query(null, statement);
        records.next();

        records.close();
        counter.getAndIncrement();
    }
}
