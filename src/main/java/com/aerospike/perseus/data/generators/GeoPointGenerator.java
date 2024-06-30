package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.data.GeoPoint;

public class GeoPointGenerator extends BaseGenerator<GeoPoint> {
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public GeoPoint next() {
       return new GeoPoint(random.nextDouble(180) - 90,
               random.nextDouble(360) - 180);
    }
}
