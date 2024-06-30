package com.aerospike.perseus.data.generators;

import java.time.Instant;
import java.time.Period;

public class DateGenerator extends BaseGenerator<Long>{
    private final Instant startInclusive = Instant.now().minus(Period.ofWeeks(540));
    private final Instant endExclusive = Instant.now();

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Long next() {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long randomDate = random.nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(randomDate).getEpochSecond();
    }
}
