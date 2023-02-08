package com.aerospike.perseus.utilities.logger;

import java.util.concurrent.atomic.AtomicInteger;

public class Total implements Logable {
    private final AtomicInteger tpsCounter = new AtomicInteger();
    @Override
    public String getHeader() {
        return "Total";
    }

    @Override
    public int getTPS() {
        return tpsCounter.getAndSet(0);
    }

    public void increment() {
        tpsCounter.incrementAndGet();
    }
}
