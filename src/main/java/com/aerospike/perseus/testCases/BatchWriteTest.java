package com.aerospike.perseus.testCases;

import com.aerospike.client.BatchRecord;
import com.aerospike.client.BatchWrite;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.collector.KeyCollector;
import com.aerospike.perseus.data.provider.DataProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchWriteTest extends Test<List<Record>>{
    private final KeyCollector<Integer> keyCollector;
    private final String namespace;
    private final String setName;

    public BatchWriteTest(AerospikeConfiguration conf, DataProvider<List<Record>> provider, KeyCollector<Integer> keyCollector) {
        super(conf, provider);
        this.keyCollector = keyCollector;
        this.namespace = conf.getNamespace();
        this.setName = conf.getSetName();
    }

    @Override
    protected void execute(List<Record> records) {
        List<BatchRecord> batchWrites = records.stream().map(r ->
                {
                    Operation[] operations = Arrays.stream(r.getBins()).map(b -> new Operation(Operation.Type.WRITE, b.name, b.value)).collect(Collectors.toList()).toArray(Operation[]::new);
                    return new BatchWrite(
                            new Key(namespace, setName, r.getKey()),
                            operations);
                })
                .collect(Collectors.toList());

        client.operate(client.batchPolicyDefault, batchWrites);
        records.stream().forEach(k -> keyCollector.collect(k.getKey()));
    }

    public String getHeader(){
        return String.format("Batch W (%d)", threadCount.get());
    }
}
