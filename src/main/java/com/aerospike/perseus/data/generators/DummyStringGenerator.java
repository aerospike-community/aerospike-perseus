package com.aerospike.perseus.data.generators;

import java.nio.charset.StandardCharsets;

public class DummyStringGenerator extends BaseGenerator<String> {
    private final double size;

    public DummyStringGenerator(double size) {
        double temp = size - 158;
        if(temp < 0){
            temp = 0;
        }
        this.size = 0;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        if(size == 0)
            return "";

        int numberOfBytes = (int) Math.ceil(
                random.nextGaussian(size, 5));
        if(numberOfBytes < 0 )
            return "";
        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
