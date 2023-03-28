package com.aerospike.perseus.utilities.logger;

import com.aerospike.perseus.domain.key.CollectorStats;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StatLogger {
    private final List<LogableTest> list;
    private final CollectorStats collectorStats;
    private final int columnWidth;
    private final String headerCellFormat;
    private final String rowCellFormat;
    private final String statRowFormat;
    private final int headerBreak;
    private final int totalWidth;
    private long i = 1;

    public StatLogger(List<LogableTest> list, CollectorStats collectorStats, int printDelay, int columnWidth, int headerBreak) {
        this.list = list;
        this.collectorStats = collectorStats;
        this.columnWidth = columnWidth;
        this.headerBreak = headerBreak;
        totalWidth = (columnWidth + 3) * (list.size() + 1) + 1;
        headerCellFormat = String.format("%s%ds |", " %-", columnWidth);
        rowCellFormat = String.format("%s%dd |", " %-", columnWidth);
        statRowFormat = String.format("| %s%ds |\n", "%-",  totalWidth-4);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::printRow, printDelay, printDelay, TimeUnit.SECONDS);
    }

    private void printRow() {
        if(i % headerBreak == 1)
            printHeader();

        String template = getTemplate(rowCellFormat, rowCellFormat);
        var time = Stream.builder().add(i++).build();
        var tps = list.stream().map(LogableTest::getTPS);
        System.out.printf(template, Stream.concat(time, tps).toArray());
    }

    private void printHeader() {
        String firstRowTemplate = getTemplate(String.format(headerCellFormat,  "Task"), headerCellFormat);
        String secondRowTemplate = getTemplate(String.format(headerCellFormat, "Thread"), headerCellFormat);
        printLine();
        System.out.printf(statRowFormat, collectorStats.getStats());
        System.out.printf(firstRowTemplate, list.stream().map(LogableTest::getHeader).toList().toArray());
        System.out.printf(secondRowTemplate, list.stream().map(LogableTest::getThreadsInformation).toList().toArray());
        printLine();
    }

    private String getTemplate(String start, String cellFormat) {
        StringBuilder format = new StringBuilder(start);
        format.append(String.valueOf(cellFormat).repeat(list.size()));
        format.append("\n");

        return "|" + format;
    }

    private void printLine() {
        System.out.println("-".repeat(totalWidth));
    }
}
