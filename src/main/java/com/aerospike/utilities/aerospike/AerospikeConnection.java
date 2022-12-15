package com.aerospike.utilities.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;

import java.util.List;

public class AerospikeConnection {
    private final AerospikeClient client;
    private final String namespace;
    private final String setName;

    private AerospikeConnection(AerospikeClient client, String namespace, String setName) {
        this.client = client;
        this.namespace = namespace;
        this.setName = setName;
    }

    public static AerospikeConnection getConnection(List<Host> hosts, String namespace, String setName){
        AerospikeClient client = new AerospikeClient(null, hosts.toArray(new Host[0]));
        return new AerospikeConnection(client, namespace, setName);
    }

    public AerospikeClient getClient() {
        return client;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getSetName() {
        return setName;
    }
}
