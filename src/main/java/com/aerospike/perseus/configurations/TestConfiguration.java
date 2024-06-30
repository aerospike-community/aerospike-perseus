package com.aerospike.perseus.configurations;

import com.aerospike.perseus.configurations.pojos.KeyCaching;

public class TestConfiguration {
    public KeyCaching keyCaching;
    public Integer recordSize;
    public Integer readBatchSize;
    public Integer writeBatchSize;
    public Double readHitRatio;
    public Boolean stringIndex;
    public Boolean numericIndex;
    public Boolean geoSpatialIndex;
    public Boolean udfAggregation;
}
