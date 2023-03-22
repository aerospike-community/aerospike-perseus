package com.aerospike.perseus.domain.data.random;

import com.aerospike.perseus.domain.data.TimePeriod;
import com.aerospike.perseus.domain.data.DataProvider;

import java.time.Instant;
import java.time.Period;
import java.util.concurrent.ThreadLocalRandom;

public class TimePeriodDataProvider implements DataProvider<TimePeriod> {
    protected final ThreadLocalRandom random;
    public TimePeriodDataProvider() {
        random = ThreadLocalRandom.current();
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
