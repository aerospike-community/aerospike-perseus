package com.aerospike.perseus.testCases;

import com.aerospike.client.*;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.BatchRecordsGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BatchWriteTest extends Test<List<Record>>{
    private final int batchSize;

    public BatchWriteTest(TestCaseConstructorArguments arguments, BatchRecordsGenerator batchSimpleRecordsGenerator, int batchSize) {
        super(arguments, batchSimpleRecordsGenerator);
        this.batchSize = batchSize;
    }

    @Override
    protected void execute(List<Record> records) {
        List<BatchRecord> batchWrites = records.stream().map(r ->
                {
                    Operation[] operations = Arrays.stream(r.getBins()).map(b -> new Operation(Operation.Type.WRITE, b.name, b.value)).toArray(Operation[]::new);
                    return new BatchWrite(
                            new Key(namespace, setName, r.getKey()),
                            operations);
                })
                .collect(Collectors.toList());

        client.operate(client.batchPolicyDefault, batchWrites);
    }

    public String[] getHeader(){
        return String.format("Batch Write\nSize: %d", batchSize).split("\n");
    }
}
