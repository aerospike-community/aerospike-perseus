package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpReadFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.utilities.SampleProvider;

import java.util.stream.Stream;

public class ExpressionReaderTest extends Test{

    private final SampleProvider<Integer> sampleProvider;

    public ExpressionReaderTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "Exp Reads");
        this.sampleProvider = sampleProvider;
    }

    protected void loop(){
        Stream.generate(() -> sampleProvider.getSample())
                .forEach(key -> put(new Key(namespace, setName, key)));
    }
    private void put(Key key) {
        Expression readExp = Exp.build(
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
        Operation readExpOp = ExpOperation.read("EXPRes",
                readExp,
                ExpReadFlags.DEFAULT);

        Record operate = client.operate(null, key, readExpOp);
//        if(operate != null)
//            System.out.println(operate.getString("r"));
        counter.getAndIncrement();
    }
}
