package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.Value;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.lua.LuaConfig;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.*;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.utilities.SampleProvider;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.stream.Stream;

public class AggregationTest extends Test{
    private final SampleProvider<Integer> sampleProvider;

    public AggregationTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
        super(client, namespace, setName, numberOfThreads, "LUAs", 1);
        this.sampleProvider = sampleProvider;
        Policy policy = new Policy(client.queryPolicyDefault);
        policy.setTimeout(120000);
        client.createIndex(null, namespace, setName, "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
        registerUDF();
    }
    void registerUDF() {
        LuaCache.clearPackages();
        String UDFFile = "agg.lua";
        client.removeUdf(null, UDFFile);
        RegisterTask task = client.register(null, "./agg.lua", UDFFile, Language.LUA);
        task.waitTillComplete();
    }
    protected void loop(){
        Stream.generate(sampleProvider::getSample)
                .forEach(this::find);
    }

    private void find(int sample){
        Statement statement = new Statement();
        long start = Instant.now().minus(Period.ofWeeks(300)).toEpochMilli();
        long now = Instant.now().toEpochMilli();
        long duration = now - start;
        long begin = random.nextLong(start, now);
        long end = begin + (duration / random.nextLong(100000, 150000));
        statement.setNamespace(namespace);
        statement.setSetName(setName);
        statement.setFilter(Filter.range("date", begin, end));
        statement.setAggregateFunction("agg", "average_range", Value.get("octet"));

        ResultSet rs = client.queryAggregate(null, statement);
        while (rs.next()) {
            Object obj = rs.getObject();
//            System.out.printf(
//                    "Stats between %s and %s (%s mins): %s\n",
//                    Instant.ofEpochMilli(begin).toString(),
//                    Instant.ofEpochMilli(end),
//                    Duration.of(end-begin, ChronoUnit.MILLIS).toMinutes(),
//                    obj.toString());
        }
        rs.close();
        counter.getAndIncrement();
    }
}
