package com.aerospike.testCases;

import com.aerospike.client.Value;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.aerospike.data.dataGenator.DataProvider;
import com.aerospike.data.dataGenator.TimePeriod;
import com.aerospike.utilities.aerospike.AerospikeConnection;

public class UDFAggregationTest extends Test<TimePeriod>{

    public UDFAggregationTest(AerospikeConnection connection, int numberOfThreads, DataProvider<TimePeriod> provider) {
        super(connection, numberOfThreads, provider);
        connection.getClient().createIndex(null, connection.getNamespace(), connection.getSetName(), "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(TimePeriod timePeriod){
        Statement statement = new Statement();


        statement.setNamespace(connection.getNamespace());
        statement.setSetName(connection.getSetName());
        statement.setFilter(Filter.range("date", timePeriod.begin(), timePeriod.end()));
        statement.setAggregateFunction("example", "average_range", Value.get("octet"));

        ResultSet rs = connection.getClient().queryAggregate(null, statement);
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

    public String getHeader(){
        return String.format("LUA Aggs (%d)", numberOfThreads);
    }
}
