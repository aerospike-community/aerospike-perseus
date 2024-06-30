package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Test<T> implements LogableTest {
    protected final AerospikeClient client;
    protected final Iterator<T> provider;
    protected final String namespace;
    protected final String setName;
    private final Thread[] threads = new Thread[maxPoolSize];
    private final boolean[] terminated = new boolean[maxPoolSize];
    private static final int maxPoolSize = 500;
    protected final AtomicInteger threadCount = new AtomicInteger(  0);
    public static final Total totalTps = new Total();
    private final AtomicInteger tpsCounter = new AtomicInteger();

    protected Test(AerospikeClient client, Iterator<T> provider, String namespace, String setName) {
        this.client = client;
        this.provider = provider;
        this.namespace = namespace;
        this.setName = setName;
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
        threads[i] = new Thread(() -> loop(i));
        threads[i].start();
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

    protected Key getKey(long key) {
        return new Key(namespace, setName, key);
    }

    @Override
    public String getThreadsInformation() {
        return String.format("%d", threadCount.get());
    }
}
