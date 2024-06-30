package com.aerospike.perseus.data.generators;

import com.aerospike.perseus.data.Record;

import java.nio.charset.StandardCharsets;

public class DummyStringGenerator extends BaseGenerator<String> {
    private final double size;

    public DummyStringGenerator(double size) {
        this.size = size - Record.SIZE;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        int numberOfBytes = (int) Math.ceil(
                random.nextGaussian(size, size / 4));
        numberOfBytes = numberOfBytes < 0 ? 10: numberOfBytes;
        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        String dummy = new String(array, StandardCharsets.UTF_8);
        return dummy;
    }
}
