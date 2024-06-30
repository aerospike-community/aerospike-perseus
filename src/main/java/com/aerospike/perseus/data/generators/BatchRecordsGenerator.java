package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.data.Record;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchRecordsGenerator extends BaseGenerator<List<Record>> {

    private final RecordGenerator simpleRecordGenerator;
    private final long batchSize;

    public BatchRecordsGenerator(RecordGenerator simpleRecordGenerator, long batchSize) {
        this.simpleRecordGenerator = simpleRecordGenerator;
        this.batchSize = batchSize;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<Record> next() {
        return Stream
                .generate(() -> simpleRecordGenerator.next())
                .limit(batchSize)
                .collect(Collectors.toList());
    }
}
