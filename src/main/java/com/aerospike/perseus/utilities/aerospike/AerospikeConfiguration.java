package com.aerospike.perseus.utilities.aerospike;

import com.aerospike.client.Host;

public interface AerospikeConfiguration {
    String getNamespace();

    String getSetName();

    Host[] getHosts();

    String getUsername();

    String getPassword();
}
