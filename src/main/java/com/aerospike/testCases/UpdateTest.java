package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.utilities.SampleProvider;

import java.time.Instant;
import java.util.stream.Stream;

public class UpdateTest extends Test{
    private final SampleProvider<Integer> sampleProvider;
    private final WritePolicy policy;

    public UpdateTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Updates", 1);
        this.sampleProvider = sampleProvider;

        policy = new WritePolicy();
        policy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    }

    protected void loop(){
        Stream.generate(sampleProvider::getSample)
                .forEach(key -> update(new Key(namespace, setName, key)));
    }

    private void update(Key key){
        Bin updated = new Bin("Updated", Instant.now().toString());
        try{
            client.add(policy, key, updated);
        } catch(Exception ignored) {

        }

        counter.getAndIncrement();
    }
}
