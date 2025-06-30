package com.aerospike.perseus.data.generators;

public class DummyBlobGenerator extends BaseGenerator<byte[]> {
    private final int size;

    public DummyBlobGenerator(int size) {
        this.size = size;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public byte[] next() {
        int numberOfBytes = size;

        byte[] repeatedPartOfArray = new byte[numberOfBytes/3];
        random.nextBytes(repeatedPartOfArray);
        byte[] array = new byte[numberOfBytes];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(repeatedPartOfArray, 0, array, i * repeatedPartOfArray.length, repeatedPartOfArray.length);
        }
        System.arraycopy(repeatedPartOfArray, 0, array, 3 * repeatedPartOfArray.length, array.length % repeatedPartOfArray.length);
        return array;
    }
}
