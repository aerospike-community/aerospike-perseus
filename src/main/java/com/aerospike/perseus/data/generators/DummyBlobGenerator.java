package com.aerospike.perseus.data.generators;

public class DummyBlobGenerator extends BaseGenerator<byte[]> {
    private final int size;

    public DummyBlobGenerator(int size) {
        int temp = size - 173;
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
        int numberOfBytes = 4;
        if(size > 9) {
            // numberOfBytes = random.nextInt( size -  size/10, size +  size/10);
            numberOfBytes = (int) random.nextGaussian( size, size/10);
            if(numberOfBytes < 1)
                return null;
        }

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
