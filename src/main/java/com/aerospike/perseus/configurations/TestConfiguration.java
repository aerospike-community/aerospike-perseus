package com.aerospike.perseus.configurations;

import com.aerospike.perseus.configurations.pojos.RangeQueryConfiguration;

public class TestConfiguration {
    public Integer recordSize;
    public Integer readBatchSize;
    public Integer writeBatchSize;
    public Double readHitRatio;
    public Boolean stringIndex;
    public Boolean numericIndex;
    public Boolean geoSpatialIndex;
    public Boolean udfAggregation;
    public RangeQueryConfiguration rangeQueryConfiguration;
    public Boolean rangeQuery;
    public int perseusId;
}
