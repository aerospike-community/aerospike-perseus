package com.aerospike.perseus.data;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class Record {
    private final long key;
    private final long keyInNumber;
    private final String keyInString;
    private final long keyPlus10;
    private final long date;
    private final Value.GeoJSONValue location;
    private final byte[] dummy;

    public static final String NUMERIC_BIN = "keyN";
    public static final String STRING_BIN = "keyS";
    public static final String GEO_BIN = "loc";
    public static final String KEY_PLUS_10 = "keyPlus10";
    public static final String DATE = "date";
    public static final String UPDATE_TIME = "updateT";
    public static final int SIZE = 300;

    public Record(long key, long date, Value.GeoJSONValue location, byte[] dummy) {
        this.key = key;
        this.keyInNumber = key;
        this.keyInString = String.valueOf(key);
        this.keyPlus10 = key+10;
        this.date = date;
        this.location = location;
        this.dummy = dummy;
    }

    public Bin[] getBins() {
        return new Bin[] {
                new Bin(NUMERIC_BIN, Value.get( this.keyInNumber)),
                new Bin(STRING_BIN, Value.get( this.keyInString)),
                new Bin(KEY_PLUS_10, Value.get( this.keyPlus10)),
                new Bin(DATE, Value.get( this.date)),
                new Bin(GEO_BIN, Value.get( this.location)),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }

    public long getKey() {
        return key;
    }
}
