package com.aerospike.perseus.utilities.logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StatLogger {
    private final List<Logable> list;
    private final int columnWidth = 15;
    private final String headerCellFormat = String.format(" %s%ds |", " %-", columnWidth);
    private final String rowCellFormat = String.format(" %s%dd |", " %-", columnWidth);

    private final int headerBreak;
    private long i = 1;

    public StatLogger(List<Logable> list, int printDelay, int headerBreak) {
        this.list = list;
        this.headerBreak = headerBreak;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::printRow, printDelay, printDelay, TimeUnit.SECONDS);
    }

    private void printRow() {
        if(i % headerBreak == 1)
            printHeader();

        String template = getTemplate(rowCellFormat, rowCellFormat);
        var time = Stream.builder().add(i++).build();
        var tps = list.stream().map(Logable::getTPS);
        System.out.printf(template, Stream.concat(time, tps).toArray());
    }

    private void printHeader() {
        String template = getTemplate(String.format(headerCellFormat, "Seconds"), headerCellFormat);
        printLine();
        System.out.printf(template, list.stream().map(Logable::getHeader).toList().toArray());
        printLine();
    }

    private String getTemplate(String start, String cellFormat) {
        StringBuilder format = new StringBuilder(start);
        format.append(String.valueOf(cellFormat).repeat(list.size()));
        format.append("\n");

        return format.toString();
    }

    private void printLine() {
        System.out.println("-".repeat(columnWidth+4).repeat(list.size()+1));
    }
}
