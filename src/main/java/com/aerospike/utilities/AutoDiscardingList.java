package com.aerospike.utilities;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AutoDiscardingList implements SampleCollector<Integer>, SampleProvider<Integer>, RandomSampleProvider<Integer> {
    private final int size;
    private final double saveRatio;
    private final double missRatio;
    private final int[] list;
    private final AtomicLong location = new AtomicLong(0);
    private final ThreadLocalRandom random;

    public AutoDiscardingList(int size, double saveRatio, double missRatio) {
        this.size = size;
        this.saveRatio = saveRatio;
        this.missRatio = missRatio;
        list = new int[size];
        random = ThreadLocalRandom.current();
    }

    public void collectSample(Integer value){
        if(random.nextFloat(0, 1) < saveRatio )
            return;
        long i = location.getAndIncrement();

        list[(int) (i % size)] = value;
    }

    public Integer getRandomSample(){
        if(random.nextFloat(0, 1) < missRatio)
            return random.nextInt();
        int l = (int) location.get();
        if(l > size)
            l = size;
        if(l == 0)
            return random.nextInt();
        return list[ThreadLocalRandom.current().nextInt(l)];
    }

    @Override
    public Integer getSample() {
        int l = (int) location.get();
        if(l > size)
            l = size;
        if(l == 0)
            return random.nextInt();
        return list[ThreadLocalRandom.current().nextInt(l)];
    }
}
