package com.aerospike.perseus.presentation;

import com.aerospike.perseus.configurations.pojos.OutputWindowConfiguration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class OutputWindow {
    private final OutputWindowConfiguration config;

    private final List<TPSLogger> tpsLoggers;
    private final String headerCellFormat;
    private final String rowCellFormat;
    private final String totalHeaderCellFormat;
    private final String totalRowCellFormat;
    private final String statRowFormat;
    private final int totalWidth;
    private long i = 1;

    public OutputWindow(OutputWindowConfiguration outputWindowConfig, List<TPSLogger> testList, TotalTpsCounter totalTpsCounter) {
        config = outputWindowConfig;

        tpsLoggers = Stream.concat(testList.stream(), Stream.of(totalTpsCounter)).toList();
        int additionalWidthForTheTotalColumn = 3;

        totalWidth = (config.columnWidth + 3) * (this.tpsLoggers.size() + 1) + 1 + additionalWidthForTheTotalColumn;
        headerCellFormat = String.format("%s%ds |", " %-", config.columnWidth);
        rowCellFormat = String.format("%s%ds |", " %-", config.columnWidth);
        totalHeaderCellFormat = String.format("%s%ds |", " %-", config.columnWidth + additionalWidthForTheTotalColumn);
        totalRowCellFormat = String.format("%s%ds |", " %-", config.columnWidth+ additionalWidthForTheTotalColumn);
        statRowFormat = String.format("| %s%ds |\n", "%-",  totalWidth-4);

        var scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                this::printRow,
                2,
                config.printIntervalSec,
                TimeUnit.SECONDS);
    }

    private void printRow() {
        if(i % config.numberOfLinesBeforeReprintingTheHeader == 1)
            printHeader();


        String template = getTemplate(rowCellFormat, rowCellFormat, totalRowCellFormat);
        var time = Stream.builder().add(i++).build();
        var tps = tpsLoggers.stream().map(TPSLogger::getTPS);
        System.out.printf(template, Stream.concat(time, tps).toArray());
    }

    private void printHeader() {
        String firstRowTemplate = getTemplate(String.format(headerCellFormat,  "Test Cases:"), headerCellFormat, totalHeaderCellFormat);
        String secondRowTemplate = getTemplate(String.format(headerCellFormat, ""), headerCellFormat, totalHeaderCellFormat);
        String thirdRowTemplate = getTemplate(String.format(headerCellFormat, "Threads:"), headerCellFormat, totalHeaderCellFormat);
        printLineOf("=");

        System.out.printf(firstRowTemplate, tpsLoggers.stream().map(t -> t.getHeader()[0]).toList().toArray());
        System.out.printf(secondRowTemplate, tpsLoggers.stream().map(t -> t.getHeader()[1]).toList().toArray());
        printLineOf("-");
        System.out.printf(thirdRowTemplate, tpsLoggers.stream().map(TPSLogger::getThreadsInformation).toList().toArray());
        printLineOf("=");
    }

    private String getTemplate(String start, String cellFormat, String lastCellFormat) {
        String format = start + String.valueOf(cellFormat).repeat(tpsLoggers.size() - 1) +
                lastCellFormat +
                "\n";

        return "|" + format;
    }

    private void printLineOf(String character) {
        System.out.println(character.repeat(totalWidth));
    }
}
