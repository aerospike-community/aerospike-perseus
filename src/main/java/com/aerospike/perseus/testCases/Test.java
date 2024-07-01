package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.perseus.presentation.TPSLogger;
import com.aerospike.perseus.presentation.TotalTpsCounter;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Test<T> extends TPSLogger {
    protected final AerospikeClient client;
    protected final Iterator<T> provider;
    protected final String namespace;
    protected final String setName;
    private final Thread[] threads = new Thread[maxPoolSize];
    private final boolean[] terminated = new boolean[maxPoolSize];
    private static final int maxPoolSize = 500;
    protected final AtomicInteger threadCount = new AtomicInteger(  0);
    private final TotalTpsCounter totalTpsCounter;

    protected Test(TestCaseConstructorArguments arguments, Iterator<T> provider) {
        this.client = arguments.client();
        this.provider = provider;
        this.namespace = arguments.namespace();
        this.setName = arguments.setName();
        this.totalTpsCounter = arguments.totalTpsCounter();
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
        totalTpsCounter.increment();
    }

    protected abstract void execute(T t);

    protected Key getKey(long key) {
        return new Key(namespace, setName, key);
    }

    @Override
    public String getThreadsInformation() {
        return String.format("%d", threadCount.get());
    }
}
