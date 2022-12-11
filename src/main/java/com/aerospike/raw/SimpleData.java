package com.aerospike.raw;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class SimpleData extends Data {
    private final int one;
    private final int number;
    private final String text;
    private final  String date;
    private final String dummy;

    public SimpleData(int key, int one, int number, String text, String date, String dummy) {
        super(key);
        this.one = one;
        this.number = number;
        this.text = text;
        this.date = date;
        this.dummy = dummy;
    }

    @Override
    public Bin[] getBins() {
        return new Bin[] {
                new Bin("one", Value.get( this.one )),
                new Bin("number", Value.get( this.number )),
                new Bin("text", Value.get( this.text )),
                new Bin("date", Value.get( this.date )),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }
}
