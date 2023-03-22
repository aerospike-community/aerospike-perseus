package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.perseus.domain.Provider;
import com.aerospike.perseus.utilities.aerospike.AerospikeClientProvider;
import com.aerospike.perseus.utilities.logger.Logable;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;
import com.aerospike.perseus.utilities.logger.Total;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Test<T> implements Logable {
    private static final int maxPoolSize = 600;
    public static final Total totalTps = new Total();
    protected final AtomicInteger threadCount = new AtomicInteger(  0);
    protected final AerospikeClient client;
    protected final AerospikeConfiguration conf;
    private final boolean[] terminated;
    private final Provider<T> provider;
    private final ThreadPoolExecutor executor;
    private final AtomicInteger tpsCounter = new AtomicInteger();

    protected Test(AerospikeConfiguration conf, Provider<T> provider) {
        this.conf = conf;
        this.provider = provider;

        client = AerospikeClientProvider.getClient(conf);

        executor = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50000));
        terminated = new boolean[maxPoolSize];
    }

    public synchronized void setThreads(int i){
        if( i > maxPoolSize )
            i = maxPoolSize;
        if( i < 0)
            i = 0;

        while(i != threadCount.get()){
            if(i < threadCount.get())
                removeThread();
            if( i > threadCount.get())
                addThread();
        }
    }

    public void addThread(){
        int i = threadCount.getAndIncrement();
        if (i >= maxPoolSize) {
            threadCount.decrementAndGet();
            return;
        }
        terminated[i] = false;
        executor.execute(() -> loop(i));
    }

    public void removeThread(){
        int i = threadCount.decrementAndGet();
        if(i < 0) {
            threadCount.incrementAndGet();
            return;
        }
        terminated[i] = true;
    }

    private void loop(int i) {
        Stream
            .generate(provider::next)
            .takeWhile(d-> checkTermination(i)).forEach(this::action);
    }

    private boolean checkTermination(int i) {
        return !terminated[i];
    }

    private void action(T t) {
        try{
            execute(t);
        } catch (Exception e) {
            e.printStackTrace();
            setThreads(0);
        }
        tpsCounter.getAndIncrement();
        totalTps.increment();
    }

    protected abstract void execute(T t);

    public int getTPS() {
        return tpsCounter.getAndSet(0);
    }

    protected Key getKey(int key) {
        return new Key(conf.getNamespace(), conf.getSetName(), key);
    }
}
