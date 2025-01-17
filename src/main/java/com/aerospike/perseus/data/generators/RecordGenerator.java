package com.aerospike.perseus.data.generators;

import com.aerospike.client.Value;
import com.aerospike.perseus.data.Record;

public class RecordGenerator extends BaseGenerator<Record> {

    private final DateGenerator dateGenerator;
    private final DummyBlobGenerator dummyBlobGenerator;
    private final GeoJsonGenerator geoJsonGenerator;

    public RecordGenerator(DateGenerator dateGenerator, DummyBlobGenerator dummyBlobGenerator, GeoJsonGenerator geoJsonGenerator) {
        this.dateGenerator = dateGenerator;
        this.dummyBlobGenerator = dummyBlobGenerator;
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
        byte[] dummy = dummyBlobGenerator.next();
        return new Record(key, date, geoJSONValue, dummy);
    }
}
