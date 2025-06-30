package com.aerospike.perseus.data.generators;

import com.aerospike.client.Value;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.key.KeyGenerator;

public class RecordGenerator extends BaseGenerator<Record> {

    private final DummyBlobGenerator dummyBlobGenerator100;
    private final DummyBlobGenerator dummyBlobGenerator2048;
    private final KeyGenerator keyGenerator;

    public RecordGenerator(DummyBlobGenerator dummyBlobGenerator100, DummyBlobGenerator dummyBlobGenerator2048, KeyGenerator keyGenerator) {
        this.dummyBlobGenerator100 = dummyBlobGenerator100;
        this.dummyBlobGenerator2048 = dummyBlobGenerator2048;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Record next() {
        Long now = keyGenerator.next();
        return new Record(now,
                dummyBlobGenerator100.next(),
                dummyBlobGenerator2048.next(),
                dummyBlobGenerator2048.next(),
                dummyBlobGenerator2048.next(),
                dummyBlobGenerator2048.next(),
                dummyBlobGenerator2048.next(),
                now,
                now,
                now,
                now);
    }
}
