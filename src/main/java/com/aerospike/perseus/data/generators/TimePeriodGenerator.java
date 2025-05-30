package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.configurations.pojos.RangeQueryConfiguration;
import com.aerospike.perseus.data.TimePeriod;
import com.aerospike.perseus.data.generators.key.KeyCache;

public class TimePeriodGenerator extends BaseGenerator<TimePeriod> {
    final double maxTimeRangeChance;
    final long maxTimeRange;
    final long normalTimeRange;
    final KeyCache cachedKeyProvider;

    public TimePeriodGenerator(RangeQueryConfiguration rangeQueryConfiguration, KeyCache cachedKeyProvider) {
        this.maxTimeRangeChance = rangeQueryConfiguration.maxTimeRangeChance;
        this.maxTimeRange = rangeQueryConfiguration.maxTimeRange;
        this.normalTimeRange = rangeQueryConfiguration.normalTimeRange;
        this.cachedKeyProvider = cachedKeyProvider;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public TimePeriod next() {
        long begin = random.nextLong(1, cachedKeyProvider.getCurrent().get());
        long end;
        if(random.nextInt(0, (int)(1d/ maxTimeRangeChance)) == 0) {
            end = begin + maxTimeRange;
        }
        else {
            double range = random.nextGaussian(normalTimeRange, normalTimeRange/4);
            long duration = (range <= 1) ? 0L : (long)range;
            end = begin + duration;
        }
        return new TimePeriod(begin, end);
    }
}
