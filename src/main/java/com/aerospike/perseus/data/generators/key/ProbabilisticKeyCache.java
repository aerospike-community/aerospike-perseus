package com.aerospike.perseus.data.generators.key;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProbabilisticKeyCache implements Iterator<Long> {
    private final KeyCache cachedKeys;
    private final double randomPercentage;
    private final ThreadLocalRandom random;

    public ProbabilisticKeyCache(KeyCache keyCache, double randomPercentage) {
        this.cachedKeys = keyCache;
        this.randomPercentage = randomPercentage;
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public final Long next() {

        if(random.nextFloat(0, 1) > randomPercentage )
            return random.nextLong();

        return cachedKeys.next();
    }
}
