package com.aerospike.perseus.testCases.search;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.data.GeoPoint;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.GeoPointGenerator;
import com.aerospike.perseus.presentation.TotalTpsCounter;
import com.aerospike.perseus.testCases.TestCaseConstructorArguments;

public class GeospatialSearchTest extends BaseSearchTest<GeoPoint> {
    public GeospatialSearchTest(TestCaseConstructorArguments arguments, GeoPointGenerator geoPointGenerator) {
        super(arguments, geoPointGenerator);
        client.createIndex(null,
                namespace,
                setName,
                "Geo_Location",
                Record.GEO_BIN,
                IndexType.GEO2DSPHERE).waitTillComplete();
        System.out.println("GeoSpatial Index was created successfully.");
    }

    @Override
    protected Statement getStatement(GeoPoint point)  {
        Statement statement = new Statement();
        statement.setFilter(Filter.geoWithinRadius(Record.GEO_BIN, point.longitude(), point.latitude(), .00001));
        statement.setNamespace(namespace);
        statement.setSetName(setName);
        return statement;
    }

    @Override
    public String[] getHeader() {
        return "Geospatial\nSearch".split("\n");
    }
}
