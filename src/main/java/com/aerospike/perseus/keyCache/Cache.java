package com.aerospike.perseus.keyCache;

import java.util.Iterator;

public interface Cache<T> extends Iterator<T> {
    void store(T value);
}
