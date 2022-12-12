package com.aerospike.raw;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class SimpleData extends Data {
    private final int octet;
    private final int keyPlus10;
    private final int keyPlus20;
    private final String country;
    private final long date;
    private final String dummy;

    public SimpleData(int key, int octet, int keyPlus10, int keyPlus20, String country, Long date, String dummy) {
        super(key);
        this.octet = octet;
        this.keyPlus10 = keyPlus10;
        this.keyPlus20 = keyPlus20;
        this.country = country;
        this.date = date;
        this.dummy = dummy;
    }

    @Override
    public Bin[] getBins() {
        return new Bin[] {
                new Bin("octet", this.octet),
                new Bin("keyPlus10", this.keyPlus10),
                new Bin("keyPlus20", this.keyPlus20),
                new Bin("country", Value.get( this.country )),
                new Bin("date", this.date),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }
}
