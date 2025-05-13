package com.aerospike.perseus.data.generators.key;

import com.aerospike.perseus.data.generators.BaseGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchedFromKeyCacheGenerator extends BaseGenerator<List<Long>> {

    private final KeyCache cache;
    private final long batchSize;

    public BatchedFromKeyCacheGenerator(KeyCache cache, long batchSize) {
        this.cache = cache;
        this.batchSize = batchSize;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<Long> next() {
        return Stream
                .generate(cache::next)
                .limit(batchSize)
                .collect(Collectors.toList());
    }
}
