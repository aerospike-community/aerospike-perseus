package com.aerospike.perseus.data.generators;

import com.aerospike.client.Value;
import com.aerospike.perseus.data.Record;

public class RecordGenerator extends BaseGenerator<Record> {

    private final DateGenerator dateGenerator;
    private final DummyStringGenerator dummyStringGenerator;
    private final GeoJsonGenerator geoJsonGenerator;

    public RecordGenerator(DateGenerator dateGenerator, DummyStringGenerator dummyStringGenerator, GeoJsonGenerator geoJsonGenerator) {
        this.dateGenerator = dateGenerator;
        this.dummyStringGenerator = dummyStringGenerator;
        this.geoJsonGenerator = geoJsonGenerator;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Record next() {
        var key = random.nextLong();
        long date = dateGenerator.next();
        Value.GeoJSONValue geoJSONValue = geoJsonGenerator.next();
        String dummy = dummyStringGenerator.next();
        return new Record(key, date, geoJSONValue, dummy);
    }
}
