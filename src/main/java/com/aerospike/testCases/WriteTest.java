package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.data.dataGenator.random.SimpleDataGenerator;
import com.aerospike.data.Data;
import com.aerospike.utilities.SampleCollector;

import java.util.stream.Stream;

public class WriteTest extends Test{

    private final SampleCollector sampleCollector;

    public WriteTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleCollector<Integer> sampleCollector) {
        super(client, namespace, setName, numberOfThreads, "Writes");
        this.sampleCollector = sampleCollector;
    }

    protected void loop(){
        Stream.generate(new SimpleDataGenerator()::next)
                .forEach(this::put);
    }
    private void put(Data sales) {
        com.aerospike.client.Key key = new Key(namespace, setName, sales.getKey());
        client.put(null, key, sales.getBins());
        sampleCollector.collectSample(sales.getKey());
        counter.getAndIncrement();
    }
}
