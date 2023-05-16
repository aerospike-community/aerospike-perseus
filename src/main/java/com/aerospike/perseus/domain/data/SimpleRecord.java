package com.aerospike.perseus.domain.data;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class SimpleRecord extends Record {
    private final int octet;
    private final long keyPlus10;
    private final long keyPlus20;
    private final String country;
    private final long date;
    private final String dummy;

    public SimpleRecord(long key, int octet, long keyPlus10, long keyPlus20, String country, Long date, String dummy) {
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
                new Bin("octet", Value.get( this.octet)),
                new Bin("keyPlus10", Value.get( this.keyPlus10)),
                new Bin("keyPlus20", Value.get( this.keyPlus20)),
                new Bin("country", Value.get( this.country )),
                new Bin("date", Value.get( this.date)),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }
}
