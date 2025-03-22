package com.aerospike.perseus.data.generators;

import java.util.concurrent.atomic.AtomicLong;

public class DateGenerator extends BaseGenerator<Long>{
    private final AtomicLong now = new AtomicLong(0);

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Long next() {
        return now.getAndIncrement();
    }

    public long getNow() {
        return now.get();
    }
}
