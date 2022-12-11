package com.aerospike.utilities;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AutoDiscardingList implements SampleCollector, SampleProvider {
    private final int size;
    private final int saveRatio;
    private final int missRatio;
    private final int[] list;
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public AutoDiscardingList(int size, int saveRatio, int missRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        this.missRatio = missRatio;
        list = new int[size];
        random = ThreadLocalRandom.current();
    }

    public void collectSample(int value){
        if(random.nextInt() % saveRatio == 0)
            return;
        long i = location.getAndIncrement();

        list[(int) (i % size)] = value;
    }

    public int getSample(){
        if(random.nextInt() % missRatio == 0)
            return random.nextInt();
        int l = (int) location.get();
        if(l > size)
            l = size;
        return list[ThreadLocalRandom.current().nextInt(l)];
    }
}
