package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.utilities.RandomSampleProvider;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class ReadTest extends Test{

    private final RandomSampleProvider<Integer> randomSampleProvider;

    public ReadTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, RandomSampleProvider<Integer> randomSampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Reads");
        this.randomSampleProvider = randomSampleProvider;
    }

    protected void loop(){
        Stream.generate(() -> randomSampleProvider.getRandomSample())
                .forEach(key -> put(new Key(namespace, setName, key)));
    }
    private void put(Key key) {
        client.get(null, key);
        counter.getAndIncrement();
    }
}
