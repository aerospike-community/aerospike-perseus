package com.aerospike.perseus;

import com.aerospike.perseus.domain.data.random.BatchSimpleRecordDataProvider;
import com.aerospike.perseus.domain.data.random.TimePeriodDataProvider;
import com.aerospike.perseus.domain.key.CertainKeyProvider;
import com.aerospike.perseus.domain.key.RandomKeyProvider;
import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.utilities.AppConfiguration;
import com.aerospike.perseus.domain.key.AutoDiscardingKeyCollector;
import com.aerospike.perseus.domain.data.random.SimpleRecordDataProvider;
import com.aerospike.perseus.utilities.ThreadAdjuster;
import com.aerospike.perseus.utilities.aerospike.LuaSetup;
import com.aerospike.perseus.utilities.logger.LogableTest;
import com.aerospike.perseus.utilities.logger.StatLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AppConfiguration conf = new AppConfiguration("configuration.yaml");
        LuaSetup.registerUDF(conf, conf.getLuaFilePath());

        var simpleDataProvider = new SimpleRecordDataProvider(conf.getSizeOfTheDummyPartOfTheRecord());
        var batchSimpleDataProvider = new BatchSimpleRecordDataProvider(simpleDataProvider, conf.getBatchSize());
        var discardingKeyCollector = new AutoDiscardingKeyCollector(
                conf.getAutoDiscardingKeyCapacity(),
                conf.getAutoDiscardingKeyRatio());
        var certainKeyProvider = new CertainKeyProvider(discardingKeyCollector);
        var randomKeyProvider = new RandomKeyProvider(discardingKeyCollector, conf.getReadHitRatio());
        var timePeriodProvider = new TimePeriodDataProvider();

        var writeTest = new WriteTest(conf, simpleDataProvider, discardingKeyCollector);
        var readTest = new ReadTest(conf, randomKeyProvider, conf.getReadHitRatio());
        var updateTest = new UpdateTest(conf, certainKeyProvider);
        var expressionReaderTest = new ExpressionReaderTest(conf, certainKeyProvider);
        var expressionWriterTest = new ExpressionWriterTest(conf, certainKeyProvider);
        var searchTest = new SearchTest(conf, certainKeyProvider);
        var udfTest = new UDFTest(conf, certainKeyProvider);
        var udfAggregationTest = new UDFAggregationTest(conf, timePeriodProvider);
        var batchWriteTest = new BatchWriteTest(conf, batchSimpleDataProvider, discardingKeyCollector, conf.getBatchSize());

        Map<String, Test> tests = new HashMap<>();
        tests.put("Write", writeTest);
        tests.put("Read", readTest);
        tests.put("Update", updateTest);
        tests.put("ExpRead", expressionReaderTest);
        tests.put("ExpWrite", expressionWriterTest);
        tests.put("UDF", udfTest);
        tests.put("Search", searchTest);
        tests.put("UDFAgg", udfAggregationTest);
        tests.put("BatchW", batchWriteTest);

        var list = new ArrayList<LogableTest>();
        list.add(writeTest);
        list.add(readTest);
        list.add(updateTest);
        list.add(expressionReaderTest);
        list.add(expressionWriterTest);
        list.add(batchWriteTest);
        list.add(searchTest);
        list.add(udfTest);
        list.add(udfAggregationTest);
        list.add(Test.totalTps);

        new StatLogger(list, conf.getPrintDelay(), conf.getColumnWidth(), conf.getHeaderBreak());

        new ThreadAdjuster(tests, conf.getThreadYamlFilePath());
    }

}