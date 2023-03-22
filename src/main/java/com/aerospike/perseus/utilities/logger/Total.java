package com.aerospike.perseus.utilities.logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Total implements Logable {
    private final AtomicInteger tpsCounter = new AtomicInteger();
    @Override
    public List<String> getHeader() {
        return List.of("Total TPS", "") ;
    }

    @Override
    public int getTPS() {
        return tpsCounter.getAndSet(0);
    }

    public void increment() {
        tpsCounter.incrementAndGet();
    }
}
