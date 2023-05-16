package com.aerospike.perseus.domain.key;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AutoDiscardingKeyCollector implements KeyCollector<Long>, CollectorStats {
    private final int size;
    private final double saveRatio;
    private final  Pair<Long, Long>[] list;
    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public AutoDiscardingKeyCollector(int size, double saveRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        list = new Pair[size];
        random = ThreadLocalRandom.current();
    }

    public void collect(Long value){
        total.getAndIncrement();
        if(random.nextFloat(0, 1) > saveRatio )
            return;
        long i = location.getAndIncrement();

        list[(int) (i % size)] = new ImmutablePair<>(value, System.currentTimeMillis());
    }

    Long next() {
        int l = (int) location.get();
        if(l > size)
            l = size;
        if(l == 0)
            return random.nextLong();
        return list[random.nextInt(l)].getKey();
    }

    @Override
    public String getStats() {
        long numberOfItems;
        var firstItem = list[100];
        var lastItem = list[size-1];
        int lastItemIndex = ((int)(location.get() - 1)% size);
        var previousItem = list[lastItemIndex];
        var pastItem = list[(int) ((location.get()+10000) % size)];
        if (firstItem == null)
            return "No Stats Populated Yet";

        long latest = previousItem.getValue();
        long earliest;
        if(lastItem != null){
            earliest = pastItem.getValue();
            numberOfItems = size;
        } else {
            earliest = firstItem.getValue();
            numberOfItems = lastItemIndex;
        }

        return String.format("Collected Keys Stat [Max: %d, Save Ratio: %d%s, # Processed: %d, # Collected: %d, Full Percent: %d%s, Duration: %s]",
                size,
                (int)(saveRatio*100),
                "%",
                total.get(),
                numberOfItems,
                100*numberOfItems/size,
                "%",
                DurationFormatUtils.formatPeriod(earliest, latest, "HH:mm:ss"));
    }
}
