package com.aerospike.perseus.data.generators;

public class DummyBlobGenerator extends BaseGenerator<byte[]> {
    private final double size;

    public DummyBlobGenerator(double size) {
        double temp = size - 158;
        if(temp < 0){
            temp = 0;
        }
        this.size = temp;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public byte[] next() {
        if(size == 0)
            return null;

        int numberOfBytes = (int) Math.ceil(
                random.nextGaussian(size, 30));
        if(numberOfBytes < 0 || numberOfBytes > size*2)
            return null;
        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        return array;
    }
}
