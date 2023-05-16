package com.aerospike.perseus.domain.key;

public class CertainKeyProvider implements KeyProvider<Long> {
    private final AutoDiscardingKeyCollector autoDiscardingKeyCollector;

    public CertainKeyProvider(AutoDiscardingKeyCollector autoDiscardingKeyCollector) {
        this.autoDiscardingKeyCollector = autoDiscardingKeyCollector;
    }

    @Override
    public Long next() {
        return autoDiscardingKeyCollector.next();
    }
}
