package com.aerospike.perseus.utilities.logger;

import java.util.List;

public interface LogableTest {
    String getHeader();
    int getTPS();

    String getThreadsInformation();
}
