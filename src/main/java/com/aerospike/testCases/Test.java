package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.utilities.Statable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Test implements Statable {
    protected final AerospikeClient client;
    protected final String namespace;
    protected final String setName;
    protected final int numberOfThreads;
    private final String printMessage;
    protected final ThreadPoolExecutor executor;
    protected AtomicInteger counter = new AtomicInteger();
    protected final ThreadLocalRandom random;

    protected Test(AerospikeClient client, String namespace, String setName, int numberOfThreads, String printMessage) {
        this.client = client;
        this.namespace = namespace;
        this.setName = setName;
        this.numberOfThreads = numberOfThreads;
        this.printMessage = printMessage;

        random = ThreadLocalRandom.current();
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(500000));
    }

    public void run() {
        for (int i = 0; i < numberOfThreads; i++)
            executor.execute(this::loop);
    }

    protected abstract void loop();

    public String getStat() {
        return String.format("%6d %-10s on %3d threads.",
                counter.getAndSet(0),
                printMessage,
                numberOfThreads);
    }
}
