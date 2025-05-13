package com.aerospike.perseus.data.generators;

import com.aerospike.client.Value;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.key.KeyGenerator;

public class RecordGenerator extends BaseGenerator<Record> {

    private final DateGenerator dateGenerator;
    private final DummyBlobGenerator dummyBlobGenerator;
    private final GeoJsonGenerator geoJsonGenerator;
    private final KeyGenerator keyGenerator;

    public RecordGenerator(DateGenerator dateGenerator, DummyBlobGenerator dummyBlobGenerator, GeoJsonGenerator geoJsonGenerator, KeyGenerator keyGenerator) {
        this.dateGenerator = dateGenerator;
        this.dummyBlobGenerator = dummyBlobGenerator;
        this.geoJsonGenerator = geoJsonGenerator;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Record next() {
        long date = dateGenerator.next();
        Value.GeoJSONValue geoJSONValue = geoJsonGenerator.next();
        byte[] dummy = dummyBlobGenerator.next();
        return new Record(keyGenerator.next(), date, geoJSONValue, dummy);
    }
}
