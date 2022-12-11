package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.random.SalesDataGenerator;
import com.aerospike.raw.Data;
import com.aerospike.utilities.SampleCollector;

import java.util.stream.Stream;

public class LoadTest extends Test{

    private final SampleCollector sampleCollector;

    public LoadTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleCollector sampleCollector) {
        super(client, namespace, setName, numberOfThreads);
        this.sampleCollector = sampleCollector;
    }

    protected void loop(){
        Stream.generate(new SalesDataGenerator()::next)
                .forEach(this::put);
    }
    private void put(Data sales) {
        com.aerospike.client.Key key = new Key(namespace, setName, sales.getKey());
        client.put(null, key, sales.getBins());
        sampleCollector.collectSample(sales.getKey());
        counter.getAndIncrement();
    }

    protected void printMessage() {
        System.out.println( counter.getAndSet(0) + " Writes Per Second.");
    }
}
