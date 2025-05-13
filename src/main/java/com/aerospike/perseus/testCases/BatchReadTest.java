package com.aerospike.perseus.testCases;

import com.aerospike.client.*;
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
        List<BatchRecord> batchReads = keys.stream()
                .map(r -> new BatchRead(new Key(namespace, setName, r), false))
                .collect(Collectors.toList());

        client.operate(client.batchPolicyDefault, batchReads);
    }

    public String[] getHeader(){
        return String.format("Batch Read\nSize: %d", batchSize).split("\n");
    }
}
