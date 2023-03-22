package com.aerospike.perseus.domain.key;

public class CertainKeyProvider implements KeyProvider<Integer> {
    private final AutoDiscardingKeyCollector autoDiscardingKeyCollector;

    public CertainKeyProvider(AutoDiscardingKeyCollector autoDiscardingKeyCollector) {
        this.autoDiscardingKeyCollector = autoDiscardingKeyCollector;
    }

    @Override
    public Integer next() {
        return autoDiscardingKeyCollector.next();
    }
}
