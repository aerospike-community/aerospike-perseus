package com.aerospike.utilities;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatLogger {
    private final List<Statable> list;
    private final int printDelay;

    private long i = 1;

    public StatLogger(List<Statable> list, int printDelay) {
        this.list = list;
        this.printDelay = printDelay;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::printMessage, printDelay, printDelay, TimeUnit.SECONDS);
    }

    private void printMessage() {
        System.out.printf(
                "--------------------<%9d>--------------------\n" +
                "In the last %d seconds, these tasks were completed:\n%s\n",
                i++,
                printDelay,
                String.join("\n", list.stream().map(l -> l.getStat()).toList()));
    }
}
