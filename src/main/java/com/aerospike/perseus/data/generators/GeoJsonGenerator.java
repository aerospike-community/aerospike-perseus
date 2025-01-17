package com.aerospike.perseus.data.generators;

import com.aerospike.client.Value;

public class GeoJsonGenerator extends BaseGenerator<Value.GeoJSONValue> {
    private final GeoPointGenerator geoPointGenerator;
    final String geoJson = "{\"type\":\"Point\",\"coordinates\":[%f,%f]}";

    public GeoJsonGenerator(GeoPointGenerator geoPointGenerator) {
        this.geoPointGenerator = geoPointGenerator;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Value.GeoJSONValue next() {
        var geoPoint = geoPointGenerator.next();
        return new Value.GeoJSONValue(
                String.format(geoJson, geoPoint.longitude(), geoPoint.latitude()));
    }
}
