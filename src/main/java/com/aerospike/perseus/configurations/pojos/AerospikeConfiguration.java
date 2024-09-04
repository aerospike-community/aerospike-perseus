package com.aerospike.perseus.configurations.pojos;

import java.util.ArrayList;
import java.util.List;

public class AerospikeConfiguration {
    public List<Host> hosts = new ArrayList<Host>();
    public String username;
    public String password;
    public String namespace;
    public String set;
    public Boolean truncateSet;

    public com.aerospike.client.Host[] getHosts() {
        return hosts.stream().map(
                        h -> new com.aerospike.client.Host(h.ip, h.port))
                .toArray(com.aerospike.client.Host[]::new);
    }
}
