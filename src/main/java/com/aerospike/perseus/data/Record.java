package com.aerospike.perseus.data;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;

public class Record {

    private final byte [] bin1;
    private final byte [] bin2;
    private final byte [] bin3;
    private final byte [] bin4;
    private final byte [] bin5;
    private final byte [] bin6;
    private final long bin7;
    private final long bin8;
    private final long bin9;
    private final long bin10;


    private final long key;

    public static final String Bin1Name = "Bin1";
    public static final String Bin2Name = "Bin2";
    public static final String Bin3Name = "Bin3";
    public static final String Bin4Name = "Bin4";
    public static final String Bin5Name = "Bin5";
    public static final String Bin6Name = "Bin6";
    public static final String Bin7Name = "Bin7";
    public static final String Bin8Name = "Bin8";
    public static final String Bin9Name = "Bin9";
    public static final String Bin10Name = "Bin10";

    public Record(long key, byte[] bin1, byte[] bin2, byte[] bin3, byte[] bin4, byte[] bin5, byte[] bin6, long bin7, long bin8, long bin9, long bin10) {
        this.key = key;
        this.bin1 = bin1;
        this.bin2 = bin2;
        this.bin3 = bin3;
        this.bin4 = bin4;
        this.bin5 = bin5;
        this.bin6 = bin6;
        this.bin7 = bin7;
        this.bin8 = bin8;
        this.bin9 = bin9;
        this.bin10 = bin10;
    }

    public Bin[] getBins() {
        return new Bin[] {
                new Bin(Bin1Name, Value.get( this.bin1)),
                new Bin(Bin2Name, Value.get( this.bin2)),
                new Bin(Bin3Name, Value.get( this.bin3)),
                new Bin(Bin4Name, Value.get( this.bin4)),
                new Bin(Bin5Name, Value.get( this.bin5)),
                new Bin(Bin6Name, Value.get( this.bin6)),
                new Bin(Bin7Name, Value.get( this.bin7)),
                new Bin(Bin8Name, Value.get( this.bin8)),
                new Bin(Bin9Name, Value.get( this.bin9)),
                new Bin(Bin10Name, Value.get( this.bin10))
        };
    }

    public long getKey() {
        return key;
    }
}
