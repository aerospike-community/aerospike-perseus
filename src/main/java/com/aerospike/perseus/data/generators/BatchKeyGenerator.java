package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.keyCache.Cache;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchKeyGenerator extends BaseGenerator<List<Long>> {

    private final Cache<Long> cache;
    private final long batchSize;

    public BatchKeyGenerator(Cache<Long> cache, long batchSize) {
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
