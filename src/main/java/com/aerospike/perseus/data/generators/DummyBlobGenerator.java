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
        int numberOfBytes = 5;
        if(size > 9) {
            numberOfBytes = random.nextInt( size -  size/10, size + 1 +  size/10);
        }

        byte[] array = new byte[numberOfBytes];
        random.nextBytes(array);
        return array;
    }
}
