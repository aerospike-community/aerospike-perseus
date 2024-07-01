package com.aerospike.perseus.presentation;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TPSLogger {
    protected final AtomicInteger tpsCounter = new AtomicInteger();

    public abstract String[] getHeader();

    public String getTPS() {
        return String.format("%,d", tpsCounter.getAndSet(0));
    }

    public abstract String getThreadsInformation();
}
