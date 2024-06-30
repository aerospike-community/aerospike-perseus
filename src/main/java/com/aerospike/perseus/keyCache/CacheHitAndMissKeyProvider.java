package com.aerospike.perseus.keyCache;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class CacheHitAndMissKeyProvider implements Iterator<Long> {
    private final KeyCache cache;
    private final double randomPercentage;
    private final ThreadLocalRandom random;

    public CacheHitAndMissKeyProvider(KeyCache autoDiscardingKeyCollector, double randomPercentage) {
        this.cache = autoDiscardingKeyCollector;
        this.randomPercentage = randomPercentage;
        random = ThreadLocalRandom.current();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Long next() {
        if(random.nextFloat(0, 1) > randomPercentage )
            return random.nextLong();

        return cache.next();
    }
}
