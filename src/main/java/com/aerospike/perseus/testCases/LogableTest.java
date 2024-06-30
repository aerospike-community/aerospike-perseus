package com.aerospike.perseus.testCases;

public interface LogableTest {
    String[] getHeader();
    int getTPS();

    String getThreadsInformation();
}
