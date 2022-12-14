package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.*;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class ExpressionWriterTest extends Test{

    private final SampleProvider<Integer> sampleProvider;

    public ExpressionWriterTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Exp Writes", 1);
        this.sampleProvider = sampleProvider;
    }

    protected void loop(){
        Stream.generate(() -> sampleProvider.getSample())
                .forEach(key -> put(new Key(namespace, setName, key)));
    }
    private void put(Key key) {
        Expression writeExp = Exp.build(
                    Exp.cond(
                        Exp.gt(
                            Exp.mul(
                                Exp.sub(
                                        Exp.intBin("keyPlus20"),
                                        Exp.intBin("keyPlus10")),
                                Exp.intBin("octet")),
                            Exp.val(100)),
                        Exp.val("Yes"),
                        Exp.val("No")));
        Operation writeExpOp = ExpOperation.write("EXPRes",
                writeExp,
                ExpWriteFlags.DEFAULT);

        WritePolicy policy = new WritePolicy(client.writePolicyDefault);
        Record operate = client.operate(policy, key, writeExpOp);
//        if(operate != null)
//            System.out.println(operate.toString());
        counter.getAndIncrement();
    }
}
