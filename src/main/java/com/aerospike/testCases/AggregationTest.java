//package com.aerospike.testCases;
//
//import com.aerospike.client.AerospikeClient;
//import com.aerospike.client.query.Filter;
//import com.aerospike.client.query.IndexType;
//import com.aerospike.client.query.RecordSet;
//import com.aerospike.client.query.Statement;
//import com.aerospike.utilities.SampleProvider;
//
//import java.time.Instant;
//import java.time.Period;
//import java.util.stream.Stream;
//
//public class AggregationTest extends Test{
//    private final SampleProvider<Integer> sampleProvider;
//
//    public AggregationTest(AerospikeClient client, String namespace, String setName, int numberOfThreads, SampleProvider<Integer> sampleProvider) {
//        super(client, namespace, setName, numberOfThreads, "Aggregations", 10);
//        this.sampleProvider = sampleProvider;
//        client.createIndex(null, namespace, setName, "indexOnDate", "date", IndexType.NUMERIC).waitTillComplete();
//    }
//
//    protected void loop(){
//        Stream.generate(sampleProvider::getSample)
//                .forEach(this::find);
//    }
//
//    private void find(int sample){
//        Statement statement = new Statement();
//        long start = Instant.now().minus(Period.ofWeeks(300)).getEpochSecond();
//        long now = Instant.now().getEpochSecond();
//        long duration = now - start;
//        statement.setFilter(Filter.range("date", random.nextLong(start, now), duration / random.nextLong(10000, 100000)));
//        statement.
//        statement.setNamespace(namespace);
//        statement.setSetName(setName);
//
//        RecordSet records = client.query(null, statement);
//        records.next();
//
//        records.close();
//        counter.getAndIncrement();
//    }
//}
