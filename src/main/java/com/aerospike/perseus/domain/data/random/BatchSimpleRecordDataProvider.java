package com.aerospike.perseus.domain.data.random;

import com.aerospike.perseus.domain.data.Record;
import com.aerospike.perseus.domain.data.DataProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchSimpleRecordDataProvider implements DataProvider<List<Record>> {

    private final SimpleRecordDataProvider simpleRecordGenerator;
    private final int batchSize;

    public BatchSimpleRecordDataProvider(SimpleRecordDataProvider simpleRecordGenerator, int batchSize) {
        this.simpleRecordGenerator = simpleRecordGenerator;
        this.batchSize = batchSize;
    }

    @Override
    public List<Record> next() {
        return Stream
                .generate(() -> simpleRecordGenerator.next())
                .limit(batchSize)
                .collect(Collectors.toList());
    }
}
