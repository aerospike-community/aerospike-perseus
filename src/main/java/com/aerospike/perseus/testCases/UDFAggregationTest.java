package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Value;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.aerospike.LuaSetup;
import com.aerospike.perseus.configurations.ResourceFileProvider;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.TimePeriod;
import com.aerospike.perseus.data.generators.TimePeriodGenerator;

import java.io.IOException;

public class UDFAggregationTest extends Test<TimePeriod>{

    public UDFAggregationTest(TestCaseConstructorArguments arguments, TimePeriodGenerator timePeriodGenerator) throws IOException {
        super(arguments, timePeriodGenerator);
       LuaSetup.registerUDF(client, ResourceFileProvider.getUdfAggregationPath());
        System.out.println("UDF Aggregation Code and the related index were registered successfully.");
        client.createIndex(null, namespace, setName, "indexOnDate", Record.DATE, IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(TimePeriod timePeriod){
        Statement statement = new Statement();

        statement.setNamespace(namespace);
        statement.setSetName(setName);
        statement.setFilter(Filter.range("date", timePeriod.begin(), timePeriod.end()));
        statement.setAggregateFunction("udf_aggregation", "average_range", Value.get(Record.NUMERIC_BIN));

        ResultSet rs = client.queryAggregate(null, statement);
        while (rs.next()) {
            Object obj = rs.getObject();
//            System.out.printf(
//                    "Stats between %s and %s (%s mins): %s\n",
//                    Instant.ofEpochSecond(begin).toString(),
//                    Instant.ofEpochSecond(end),
//                    Duration.of(end-begin, ChronoUnit.SECONDS).toMinutes(),
//                    obj.toString());
        }
        rs.close();
    }

    public String[] getHeader(){
        return "UDF\nAggregate".split("\n");
    }
}
