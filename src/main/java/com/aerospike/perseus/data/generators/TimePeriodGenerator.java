package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.configurations.pojos.RangeQueryConfiguration;
import com.aerospike.perseus.data.TimePeriod;

public class TimePeriodGenerator extends BaseGenerator<TimePeriod> {
    final DateGenerator dateGenerator;
    final double maxTimeRangeChance;
    final long maxTimeRange;
    final long normalTimeRange;

    public TimePeriodGenerator(DateGenerator dateGenerator, RangeQueryConfiguration rangeQueryConfiguration) {
        this.dateGenerator = dateGenerator;
        this.maxTimeRangeChance = rangeQueryConfiguration.maxTimeRangeChance;
        this.maxTimeRange = rangeQueryConfiguration.maxTimeRange;
        this.normalTimeRange = rangeQueryConfiguration.normalTimeRange;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public TimePeriod next() {
        long start = 1;
        long now = dateGenerator.getNow();
        long begin = random.nextLong(start, now);

        if(random.nextInt(0, (int)(1d/ maxTimeRangeChance)) == 0) {
            return new TimePeriod(begin, begin + maxTimeRange);
        }
        else {
            double sum = 0;
            for (int i = 0; i < normalTimeRange; i++) {
                sum += random.nextDouble();
            }
            long duration = (long) (normalTimeRange * (Math.abs(-((double)normalTimeRange/2) + sum) / normalTimeRange));
            long end = begin + duration;

            return new TimePeriod(begin, end);
        }
    }
}
