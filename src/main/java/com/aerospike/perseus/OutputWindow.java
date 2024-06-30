package com.aerospike.perseus;

import com.aerospike.perseus.keyCache.CacheStats;
import com.aerospike.perseus.configurations.pojos.OutputWindowConfiguration;
import com.aerospike.perseus.testCases.LogableTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class OutputWindow {
    private final List<LogableTest> list;
    private final CacheStats cacheStats;
    private final OutputWindowConfiguration config;

    private final String headerCellFormat;
    private final String rowCellFormat;
    private final String statRowFormat;
    private final int totalWidth;
    private long i = 1;

    public OutputWindow(OutputWindowConfiguration outputWindowConfig, ArrayList<LogableTest> testList, CacheStats keyCache) {
        config = outputWindowConfig;
        list = testList;
        cacheStats = keyCache;

        totalWidth = (config.columnWidth + 3) * (list.size() + 1) + 1;
        headerCellFormat = String.format("%s%ds |", " %-", config.columnWidth);
        rowCellFormat = String.format("%s%dd |", " %-", config.columnWidth);
        statRowFormat = String.format("| %s%ds |\n", "%-",  totalWidth-4);

        var scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                this::printRow,
                config.printIntervalSec,
                config.printIntervalSec,
                TimeUnit.SECONDS);
    }

    private void printRow() {
        if(i % config.numberOfLinesBeforeReprintingTheHeader == 1)
            printHeader();


        String template = getTemplate(rowCellFormat, rowCellFormat);
        var time = Stream.builder().add(i++).build();
        var tps = list.stream().map(LogableTest::getTPS);
        System.out.printf(template, Stream.concat(time, tps).toArray());
    }

    private void printHeader() {
        String firstRowTemplate = getTemplate(String.format(headerCellFormat,  "Task"), headerCellFormat);
        String secondRowTemplate = getTemplate(String.format(headerCellFormat, "Name"), headerCellFormat);
        String thirdRowTemplate = getTemplate(String.format(headerCellFormat, "Thread"), headerCellFormat);
        printLineOf("=");
        System.out.printf(statRowFormat, cacheStats.getStats());
        printLineOf("-");
        System.out.printf(firstRowTemplate, list.stream().map(t -> t.getHeader()[0]).toList().toArray());
        System.out.printf(secondRowTemplate, list.stream().map(t -> t.getHeader()[1]).toList().toArray());
        System.out.printf(thirdRowTemplate, list.stream().map(LogableTest::getThreadsInformation).toList().toArray());
        printLineOf("=");
    }

    private String getTemplate(String start, String cellFormat) {
        StringBuilder format = new StringBuilder(start);
        format.append(String.valueOf(cellFormat).repeat(list.size()));
        format.append("\n");

        return "|" + format;
    }

    private void printLineOf(String character) {
        System.out.println(character.repeat(totalWidth));
    }
}
