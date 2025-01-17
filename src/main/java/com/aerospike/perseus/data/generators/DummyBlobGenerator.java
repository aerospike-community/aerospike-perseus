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
        if(size == 0)
            return null;

        int numberOfBytes = random.nextInt(0, size * 2);
        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        return array;
    }
}
