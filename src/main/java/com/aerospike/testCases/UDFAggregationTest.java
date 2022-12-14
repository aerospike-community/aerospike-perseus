package com.aerospike.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.Value;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.utilities.SampleProvider;

import java.time.Instant;
import java.time.Period;
import java.util.stream.Stream;

public class UDFAggregationTest extends Test{
    private final SampleProvider<Integer> sampleProvider;

    public UDFAggregationTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
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
        Stream.generate(sampleProvider::getRandomSample)
                .forEach(this::find);
    }

    private void find(int sample){
        Statement statement = new Statement();
        long start = Instant.now().minus(Period.ofWeeks(299)).getEpochSecond();
        long now = Instant.now().getEpochSecond();
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
//                    Instant.ofEpochSecond(begin).toString(),
//                    Instant.ofEpochSecond(end),
//                    Duration.of(end-begin, ChronoUnit.SECONDS).toMinutes(),
//                    obj.toString());
        }
        rs.close();
        counter.getAndIncrement();
    }
}
