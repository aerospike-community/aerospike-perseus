package com.aerospike.perseus.data.generators;

public class DummyBlobGenerator extends BaseGenerator<byte[]> {
    private final int size;

    public DummyBlobGenerator(int size) {
        int temp = size - 158;
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
        int numberOfBytes = 2;
        if(size > 4) {
            numberOfBytes = random.nextInt(9 * size / 10, 11 * size / 10);
        }

        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        return array;
    }
}
