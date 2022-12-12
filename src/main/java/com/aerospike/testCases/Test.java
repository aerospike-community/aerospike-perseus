package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Test {
    protected final AerospikeClient client;
    protected final String namespace;
    protected final String setName;
    protected final int numberOfThreads;
    private final String printMessage;
    private final int printDelay;
    protected final ThreadPoolExecutor executor;
    protected AtomicInteger counter = new AtomicInteger();
    protected final ThreadLocalRandom random;

    protected Test(AerospikeClient client, String namespace, String setName, int numberOfThreads, String printMessage, int printDelay) {
        this.client = client;
        this.namespace = namespace;
        this.setName = setName;
        this.numberOfThreads = numberOfThreads;
        this.printMessage = printMessage;
        this.printDelay = printDelay;

        random = ThreadLocalRandom.current();
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(500000));
    }

    public void run() {
        for (int i = 0; i < numberOfThreads; i++)
            executor.execute(this::loop);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::printMessage, printDelay, printDelay, TimeUnit.SECONDS);
    }

    protected abstract void loop();


    protected void printMessage() {
        System.out.printf("Number of %s on\t%d threads in the last\t%s Second(s):\t%d%n",
                printMessage,
                numberOfThreads,
                printDelay,
                counter.getAndSet(0));
    }
}
