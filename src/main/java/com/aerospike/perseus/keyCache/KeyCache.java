package com.aerospike.perseus.keyCache;

import com.aerospike.perseus.presentation.CacheStats;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class KeyCache implements Cache<Long>, CacheStats {
    private final int size;
    private final double saveRatio;
    private final  Pair<Long, Long>[] list;
    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public KeyCache(int size, double saveRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        list = new Pair[size];
        random = ThreadLocalRandom.current();
    }

    public void store(Long value){
        total.getAndIncrement();
        if(random.nextFloat(0, 1) > saveRatio )
            return;
        long i = location.getAndIncrement();

        list[(int) (i % size)] = new ImmutablePair<>(value, System.currentTimeMillis());
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Long next() {
        int l = (int) location.get();
        if(l > size)
            l = size;
        if(l == 0)
            return random.nextLong();
        Pair<Long, Long> pair = list[random.nextInt(l)];
        if(pair == null)
            return 0L;
        return pair.getKey();
    }

    @Override
    public String getStats() {
        long numberOfItems;
        var firstItem = list[100];
        var lastItem = list[size-1];
        int lastItemIndex = ((int)(location.get() - 1)% size);
        if(lastItemIndex<0)
            return "No Stats Populated Yet";

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

        return String.format("Written Records Count: %,d | Key Cache Capacity: %d, Save Rate: %d%s, Cache Full: %d%s, Cache Duration: %s",
                numberOfItems,
                size,
                (int)(saveRatio*100),
                "%",
                100*numberOfItems/size,
                "%",
                DurationFormatUtils.formatPeriod(earliest, latest, "HH:mm:ss"));
    }
}
