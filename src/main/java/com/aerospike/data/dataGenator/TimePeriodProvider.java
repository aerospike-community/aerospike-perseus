package com.aerospike.data.dataGenator;

import java.time.Instant;
import java.time.Period;
import java.util.concurrent.ThreadLocalRandom;

public class TimePeriodProvider implements DataProvider<TimePeriod> {
    protected final ThreadLocalRandom random;

    public TimePeriodProvider() {
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
