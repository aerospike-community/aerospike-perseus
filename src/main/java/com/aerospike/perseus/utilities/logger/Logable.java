package com.aerospike.perseus.utilities.logger;

import java.util.List;

public interface Logable {
    List<String> getHeader();
    int getTPS();
}
