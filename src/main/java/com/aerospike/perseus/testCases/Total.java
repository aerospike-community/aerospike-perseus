package com.aerospike.perseus.testCases;

import java.util.concurrent.atomic.AtomicInteger;

public class Total implements LogableTest {
    private final AtomicInteger tpsCounter = new AtomicInteger();
    @Override
    public String[] getHeader() {
        return "Total\nTPS".split("\n") ;
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
