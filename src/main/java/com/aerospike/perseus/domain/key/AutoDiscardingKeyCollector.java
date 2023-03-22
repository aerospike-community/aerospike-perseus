package com.aerospike.perseus.domain.key;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AutoDiscardingKeyCollector implements KeyCollector<Integer> {
    private final int size;
    private final double saveRatio;
    private final int[] list;
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public AutoDiscardingKeyCollector(int size, double saveRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        list = new int[size];
        random = ThreadLocalRandom.current();
    }

    public void collect(Integer value){
        if(random.nextFloat(0, 1) < saveRatio )
            return;
        long i = location.getAndIncrement();

        list[(int) (i % size)] = value;
    }

    Integer next() {
        int l = (int) location.get();
        if(l > size)
            l = size;
        if(l == 0)
            return random.nextInt();
        return list[random.nextInt(l)];
    }
}