package com.aerospike;

import com.aerospike.data.collector.AutoDiscardingKeyCollector;
import com.aerospike.data.provider.random.SimpleRecordProvider;
import com.aerospike.data.provider.random.TimePeriodProvider;
import com.aerospike.testCases.*;
import com.aerospike.utilities.AppConfiguration;
import com.aerospike.utilities.ThreadAdjuster;
import com.aerospike.utilities.aerospike.LuaSetup;
import com.aerospike.utilities.logger.StatLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AppConfiguration conf = new AppConfiguration("configuration.yaml");
        LuaSetup.registerUDF(conf, conf.getLuaFilePath());

        var simpleDataProvider = new SimpleRecordProvider();
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

        new StatLogger(new ArrayList(tests.values()), conf.getPrintDelay());

        new ThreadAdjuster(tests, conf.getThreadConfFilePath());
    }

}