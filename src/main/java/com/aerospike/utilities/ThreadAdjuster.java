package com.aerospike.utilities;

import com.aerospike.testCases.Test;
import com.aerospike.utilities.aerospike.LuaSetup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadAdjuster {
    private final Map<String, Test> tests;
    private final String fileName;

    public ThreadAdjuster(Map<String, Test> tests, String fileName) {
        this.tests = tests;
        this.fileName = fileName;

        setupFile();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(this::checkThreads, 0, 1, TimeUnit.SECONDS);
    }

    private void setupFile() {
        InputStream testThreads = LuaSetup.class.getClassLoader().getResourceAsStream("testThreads.conf");
        try {
            Path path = Paths.get(fileName);
            if(Files.exists(path))
                return;

            java.nio.file.Files.copy(
                    testThreads,
                    path,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {

        }
    }

    private void checkThreads() {
        try {
            var reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                String[] split = line.split(": ");
                tests.get(split[0]).setThreads(Integer.parseInt(split[split.length - 1]));
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
