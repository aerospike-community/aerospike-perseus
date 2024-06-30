package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.data.TimePeriod;

import java.time.Instant;
import java.time.Period;

public class TimePeriodGenerator extends BaseGenerator<TimePeriod> {
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public TimePeriod next() {
        long start = Instant.now().minus(Period.ofWeeks(299)).getEpochSecond();
        long now = Instant.now().getEpochSecond();
        long duration = now - start;
        long begin = random.nextLong(start, now);
        long end = begin + (duration / random.nextLong(100000, 150000));

        return new TimePeriod(begin, end);
    }
}
