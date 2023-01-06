package com.aerospike.utilities;

import com.aerospike.testCases.Test;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadAdjuster {
    private final Map<String, Test> tests;
    private final ConfigFileProvider configFileProvider;

    public ThreadAdjuster(Map<String, Test> tests, String fileName) {
        this.tests = tests;
        configFileProvider = new ConfigFileProvider(fileName, "threads.yaml");

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(this::checkThreads, 0, 1, TimeUnit.SECONDS);
    }


    private void checkThreads() {
        Map<String, Object> conf = configFileProvider.ReadYaml();
        conf.keySet().forEach(k -> tests.get(k).setThreads((Integer) conf.get(k)));
    }
}
