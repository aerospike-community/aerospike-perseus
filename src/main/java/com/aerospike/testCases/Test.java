package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Test {
    protected final AerospikeClient client;
    protected final String namespace;
    protected final String setName;
    protected final int numberOfThreads;
    protected final ThreadPoolExecutor executor;
    protected AtomicInteger counter = new AtomicInteger();

    protected Test(AerospikeClient client, String namespace, String setName, int numberOfThreads) {
        this.client = client;
        this.namespace = namespace;
        this.setName = setName;
        this.numberOfThreads = numberOfThreads;
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(500000));
    }

    public void run() {
        for (int i = 0; i < numberOfThreads; i++)
            executor.execute(() -> loop());

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> printMessage(), 1, 1, TimeUnit.SECONDS);
    }

    protected abstract void loop();

    protected abstract void printMessage();
}
