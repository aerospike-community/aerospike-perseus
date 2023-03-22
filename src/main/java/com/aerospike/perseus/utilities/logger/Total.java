package com.aerospike.perseus.utilities.logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Total implements LogableTest {
    private final AtomicInteger tpsCounter = new AtomicInteger();
    @Override
    public String getHeader() {
        return "Total TPS" ;
    }

    @Override
    public int getTPS() {
        return tpsCounter.getAndSet(0);
    }

    @Override
    public String getThreadsInformation() {
        return "";
    }

    public void increment() {
        tpsCounter.incrementAndGet();
    }
}
