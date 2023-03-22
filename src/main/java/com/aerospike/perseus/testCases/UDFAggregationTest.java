package com.aerospike.perseus.testCases;

import com.aerospike.client.Value;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.domain.data.DataProvider;
import com.aerospike.perseus.domain.data.TimePeriod;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class UDFAggregationTest extends Test<TimePeriod>{

    public UDFAggregationTest(AerospikeConfiguration connection, DataProvider<TimePeriod> provider) {
        super(connection, provider);
        client.createIndex(null, connection.getNamespace(), connection.getSetName(), "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
    }

    protected void execute(TimePeriod timePeriod){
        Statement statement = new Statement();

        statement.setNamespace(conf.getNamespace());
        statement.setSetName(conf.getSetName());
        statement.setFilter(Filter.range("date", timePeriod.begin(), timePeriod.end()));
        statement.setAggregateFunction("example", "average_range", Value.get("octet"));

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

    public List<String> getHeader(){
        return List.of("UDF Aggregate", String.format("%d", threadCount.get()));
    }
}
