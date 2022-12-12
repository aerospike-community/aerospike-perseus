package com.aerospike.raw;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class SimpleData extends Data {
    private final int one;
    private final int keyPlus10;
    private final int keyPlus20;
    private final String country;
    private final long date;
    private final String dummy;

    public SimpleData(int key, int one, int keyPlus10, int keyPlus20, String country, Long date, String dummy) {
        super(key);
        this.one = one;
        this.keyPlus10 = keyPlus10;
        this.keyPlus20 = keyPlus20;
        this.country = country;
        this.date = date;
        this.dummy = dummy;
    }

    @Override
    public Bin[] getBins() {
        return new Bin[] {
                new Bin("one", Value.get( this.one )),
                new Bin("keyPlus10", Value.get( this.keyPlus10)),
                new Bin("keyPlus20", Value.get( this.keyPlus20)),
                new Bin("country", Value.get( this.country )),
                new Bin("date", Value.get( this.date )),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }
}
