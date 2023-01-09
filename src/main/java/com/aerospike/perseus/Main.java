package com.aerospike.perseus;

import com.aerospike.perseus.data.provider.random.TimePeriodProvider;
import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.utilities.AppConfiguration;
import com.aerospike.perseus.data.collector.AutoDiscardingKeyCollector;
import com.aerospike.perseus.data.provider.random.SimpleRecordProvider;
import com.aerospike.perseus.utilities.ThreadAdjuster;
import com.aerospike.perseus.utilities.aerospike.LuaSetup;
import com.aerospike.perseus.utilities.logger.StatLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AppConfiguration conf = new AppConfiguration("configuration.yaml");
        LuaSetup.registerUDF(conf, conf.getLuaFilePath());

        var simpleDataProvider = new SimpleRecordProvider(conf.getSizeOfTheDummyPartOfTheRecord());
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

        Map<String, Test> tests = new HashMap<>();
        tests.put("Write", writeTest);
        tests.put("Read", readTest);
        tests.put("Update", updateTest);
        tests.put("ExpRead", expressionReaderTest);
        tests.put("ExpWrite", expressionWriterTest);
        tests.put("UDF", udfTest);
        tests.put("Search", searchTest);
        tests.put("UDFAgg", udfAggregationTest);

        new StatLogger(new ArrayList(tests.values()), conf.getPrintDelay(), conf.getHeaderBreak());

        new ThreadAdjuster(tests, conf.getThreadYamlFilePath());
    }

}