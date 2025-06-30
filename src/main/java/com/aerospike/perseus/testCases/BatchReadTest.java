package com.aerospike.perseus.testCases;

import com.aerospike.client.*;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpReadFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.perseus.data.generators.key.BatchedFromKeyCacheGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class BatchReadTest extends Test<List<Long>>{
    private final int batchSize;

    public BatchReadTest(TestCaseConstructorArguments arguments, BatchedFromKeyCacheGenerator generator, int batchSize) {
        super(arguments, generator);
        this.batchSize = batchSize;
    }

    @Override
    protected void execute(List<Long> keys) {
        Expression readExp = Exp.build(
                Exp.div(
                        Exp.mul(
                                Exp.mul(
                                        Exp.intBin("Bin7"),
                                        Exp.intBin("Bin8")
                                ),
                                Exp.intBin("Bin9")
                        ),
                        Exp.intBin("Bin10")));
        Operation readExpOp = ExpOperation.read("ExpRes",
                readExp,
                ExpReadFlags.DEFAULT);

        var operation = new Operation[] {
                Operation.get("Bin1"),
                Operation.get("Bin2"),
                Operation.get("Bin3"),
                Operation.get("Bin4"),
                Operation.get("Bin5"),
                Operation.get("Bin6"),
                Operation.get("Bin7"),
                Operation.get("Bin8"),
                Operation.get("Bin9"),
                Operation.get("Bin10"),
                readExpOp };

        List<BatchRecord> batchReads = keys.stream()
                .map(r -> new BatchRead(new Key(namespace, setName, r),  operation))
                .collect(Collectors.toList());

        var res = client.operate(client.batchPolicyDefault, batchReads);
    }

    public String[] getHeader(){
        return String.format("Batch Read\nSize: %d", batchSize).split("\n");
    }
}
