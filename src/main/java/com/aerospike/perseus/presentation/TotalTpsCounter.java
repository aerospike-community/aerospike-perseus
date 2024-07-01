package com.aerospike.perseus.presentation;

public class TotalTpsCounter extends TPSLogger {
    @Override
    public String[] getHeader() {
        return "Total TPS\n ".split("\n") ;
    }

    @Override
    public String getThreadsInformation() {
        return "";
    }

    public void increment() {
        tpsCounter.incrementAndGet();
    }
}
