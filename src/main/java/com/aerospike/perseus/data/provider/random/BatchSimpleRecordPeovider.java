package com.aerospike.perseus.data.provider.random;

import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.provider.DataProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchSimpleRecordPeovider implements DataProvider<List<Record>> {

    private final SimpleRecordProvider simpleRecordProvider;
    private final int batchSize;

    public BatchSimpleRecordPeovider(SimpleRecordProvider simpleRecordProvider, int batchSize) {
        this.simpleRecordProvider = simpleRecordProvider;
        this.batchSize = batchSize;
    }

    @Override
    public List<Record> next() {
        return Stream
                .generate(() -> simpleRecordProvider.next())
                .limit(batchSize)
                .collect(Collectors.toList());
    }
}
