package com.aerospike.perseus.keyCache;

import com.aerospike.perseus.presentation.CacheStats;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class KeyCache implements Cache<Long>, CacheStats {
    private final int size;
    private final double saveRatio;
    private final Long[] cache;
    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public KeyCache(int size, double saveRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        cache = new Long[size];
        random = ThreadLocalRandom.current();
    }

    public void store(Long value){
        total.getAndIncrement();
        if(random.nextFloat(0, 1) > saveRatio )
            return;
        int i = (int) (location.getAndIncrement()% size);

        cache[i] = value;
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
        return cache[random.nextInt(l)];
    }

    @Override
    public String getStats() {
        long numberOfItems;

        long total = this.total.get();
        if(size > total)
            numberOfItems = total;
        else
            numberOfItems = size;

        return String.format("Written Records Count: %,d | Key Cache Capacity: %,d, Save Rate: %d%s, Cache Full: %d%s",
                total,
                size,
                (int)(saveRatio*100),
                "%",
                100*numberOfItems/size,
                "%");
    }
}
