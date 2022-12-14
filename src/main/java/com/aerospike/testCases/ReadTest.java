package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class ReadTest extends Test{

    private final SampleProvider<Integer> sampleProvider;

    public ReadTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Reads", 1);
        this.sampleProvider = sampleProvider;
    }

    protected void loop(){
        Stream.generate(() -> sampleProvider.getRandomSample())
                .forEach(key -> put(new Key(namespace, setName, key)));
    }
    private void put(Key key) {
        client.get(null, key);
        counter.getAndIncrement();
    }
}
