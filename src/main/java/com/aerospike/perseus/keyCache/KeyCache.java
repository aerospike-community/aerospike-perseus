package com.aerospike.perseus.keyCache;

import com.aerospike.perseus.presentation.CacheStats;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class KeyCache implements Cache<Long>, CacheStats {
    private final int size;
    private final double saveRatio;
    private final  Long[] listKey;
    private final  Long[] listValue;
    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public KeyCache(int size, double saveRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        listKey = new Long[size];
        listValue = new Long[size];
        random = ThreadLocalRandom.current();
    }

    public void store(Long value){
        total.getAndIncrement();
        if(random.nextFloat(0, 1) > saveRatio )
            return;
        int i = (int) (location.getAndIncrement()% size);

        listValue[i] = value;
        listKey[i] = System.currentTimeMillis();
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
        return listKey[random.nextInt(l)];
    }

    @Override
    public String getStats() {
        long numberOfItems;
        var firstItem = listValue[0];
        var lastItem = listValue[size-1];
        int currentLatestItemIndex = ((int)(location.get() - 1)% size);
        if(currentLatestItemIndex < 0)
            return "No Stats Populated Yet";

        var currentLatestItem = listValue[currentLatestItemIndex];
        var pastItem = listValue[(int) ((location.get()+10000) % size)];
        if (firstItem == 0)
            return "No Stats Populated Yet";

        long latest = currentLatestItem;
        long earliest;
        if(lastItem != 0){
            earliest = pastItem;
            numberOfItems = size;
        } else {
            earliest = firstItem;
            numberOfItems = currentLatestItemIndex;
        }

        return String.format("Written Records Count: %,d | Key Cache Capacity: %,d, Save Rate: %d%s, Cache Full: %d%s, Cache Duration: %s",
                total.get(),
                size,
                (int)(saveRatio*100),
                "%",
                100*numberOfItems/size,
                "%",
                DurationFormatUtils.formatPeriod(earliest, latest, "HH:mm:ss"));
    }
}
