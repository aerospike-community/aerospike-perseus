package com.aerospike.perseus.testCases.search;

import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.*;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.TimePeriod;
import com.aerospike.perseus.data.generators.TimePeriodGenerator;
import com.aerospike.perseus.testCases.Test;
import com.aerospike.perseus.testCases.TestCaseConstructorArguments;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class RangeQueryTest extends Test<TimePeriod> {

    ThreadLocalRandom random;
    public RangeQueryTest(TestCaseConstructorArguments arguments, TimePeriodGenerator timePeriodGenerator) throws IOException {
        super(arguments, timePeriodGenerator);
        client.createIndex(null, namespace, setName, "indexOnDate", Record.DATE, IndexType.NUMERIC).waitTillComplete();
        random = ThreadLocalRandom.current();
    }

    protected void execute(TimePeriod timePeriod){
        Statement statement = new Statement();

        statement.setNamespace(namespace);
        statement.setSetName(setName);
        statement.setFilter(Filter.range("date", timePeriod.begin(), timePeriod.end()));
        QueryPolicy queryPolicy = new QueryPolicy();
        queryPolicy.shortQuery = timePeriod.end() - timePeriod.begin() < 5;

        RecordSet rs = client.query(queryPolicy, statement);
        while (rs.next()) {
            Object obj = rs.getRecord();
        }
        rs.close();
    }

    public String[] getHeader(){
        return "Range\nQuery ".split("\n");
    }
}
