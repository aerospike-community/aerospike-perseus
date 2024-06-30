package com.aerospike.perseus.data.generators;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseGenerator<T> implements Iterator<T> {
    protected final ThreadLocalRandom random;
    public BaseGenerator() {
        random = ThreadLocalRandom.current();
    }

}
