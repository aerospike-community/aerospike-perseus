package com.aerospike.perseus.domain.key;

import java.util.concurrent.ThreadLocalRandom;

public class RandomKeyProvider implements KeyProvider<Long> {
    private final AutoDiscardingKeyCollector autoDiscardingKeyCollector;
    private final double randomPercentage;
    private final ThreadLocalRandom random;

    public RandomKeyProvider(AutoDiscardingKeyCollector autoDiscardingKeyCollector, double randomPercentage) {
        this.autoDiscardingKeyCollector = autoDiscardingKeyCollector;
        this.randomPercentage = randomPercentage;
        random = ThreadLocalRandom.current();
    }

    @Override
    public Long next() {
        if(random.nextFloat(0, 1) > randomPercentage )
            return random.nextLong();

        return autoDiscardingKeyCollector.next();
    }
}
