package com.aerospike.perseus;

import com.aerospike.perseus.data.provider.random.BatchSimpleRecordPeovider;
import com.aerospike.perseus.data.provider.random.TimePeriodProvider;
import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.utilities.AppConfiguration;
import com.aerospike.perseus.data.collector.AutoDiscardingKeyCollector;
import com.aerospike.perseus.data.provider.random.SimpleRecordProvider;
import com.aerospike.perseus.utilities.ThreadAdjuster;
import com.aerospike.perseus.utilities.aerospike.LuaSetup;
import com.aerospike.perseus.utilities.logger.Logable;
import com.aerospike.perseus.utilities.logger.StatLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AppConfiguration conf = new AppConfiguration("configuration.yaml");
        LuaSetup.registerUDF(conf, conf.getLuaFilePath());

        var simpleDataProvider = new SimpleRecordProvider(conf.getSizeOfTheDummyPartOfTheRecord());
        var batchSimpleDataProvider = new BatchSimpleRecordPeovider(simpleDataProvider, conf.getBatchSize());
        var discardingKeyList = new AutoDiscardingKeyCollector(
                conf.getAutoDiscardingKeyCapacity(),
                conf.getAutoDiscardingKeyRatio());
        var timePeriodProvider = new TimePeriodProvider();

        var writeTest = new WriteTest(conf, simpleDataProvider, discardingKeyList);
        var readTest = new ReadTest(conf, discardingKeyList);
        var updateTest = new UpdateTest(conf, discardingKeyList);
        var expressionReaderTest = new ExpressionReaderTest(conf, discardingKeyList);
        var expressionWriterTest = new ExpressionWriterTest(conf, discardingKeyList);
        var searchTest = new SearchTest(conf, discardingKeyList);
        var udfTest = new UDFTest(conf, discardingKeyList);
        var udfAggregationTest = new UDFAggregationTest(conf, timePeriodProvider);
        var batchWriteTest = new BatchWriteTest(conf, batchSimpleDataProvider, discardingKeyList);

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

        var list = new ArrayList<Logable>();
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