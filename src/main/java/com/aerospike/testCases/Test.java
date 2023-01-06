package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.data.provider.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeClientProvider;
import com.aerospike.utilities.aerospike.AerospikeConfiguration;
import com.aerospike.utilities.logger.Logable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Test<T> implements Logable {
    private static final int maxPoolSize = 200;
    protected final AtomicInteger threadCount = new AtomicInteger(  0);
    protected final AerospikeClient client;
    protected final AerospikeConfiguration conf;
    private final boolean[] terminated;
    private final DataProvider<T> dataProvider;
    private final ThreadPoolExecutor executor;
    private final AtomicInteger tpsCounter = new AtomicInteger();

    protected Test(AerospikeConfiguration conf, DataProvider<T> dataProvider) {
        this.conf = conf;
        this.dataProvider = dataProvider;

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
            .generate(dataProvider::next)
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
    }

    protected abstract void execute(T t);

    public int getTPS() {
        return tpsCounter.getAndSet(0);
    }

    protected Key getKey(int key) {
        return new Key(conf.getNamespace(), conf.getSetName(), key);
    }
}
