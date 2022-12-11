package com.aerospike.raw;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class SalesData extends Data {
    private final String segment;
    private final String country;
    private final String product;
    private final double unitsSold;
    private final double mfgPrice;
    private final double salesPrice;
    private final  String date;

    private final String dummy;

    public SalesData(int key, String segment, String country, String product, double unitsSold, double mfgPrice, double salesPrice, String date, String dummy) {
        super(key);
        this.segment = segment;
        this.country = country;
        this.product = product;
        this.unitsSold = unitsSold;
        this.mfgPrice = mfgPrice;
        this.salesPrice = salesPrice;
        this.date = date;
        this.dummy = dummy;
    }

    @Override
    public Bin[] getBins() {
        return new Bin[] {
                new Bin("segment", Value.get( this.segment )),
                new Bin("country", Value.get( this.country )),
                new Bin("product", Value.get( this.product )),
                new Bin("unitsSold", Value.get( this.unitsSold )),
                new Bin("mfgPrice", Value.get( this.mfgPrice )),
                new Bin("salesPrice", Value.get( this.salesPrice )),
                new Bin("date", Value.get( this.date )),
                new Bin("dummy", Value.get( this.dummy ))
        };
    }
}
