package com.aerospike.perseus.utilities.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;

public class AerospikeClientProvider {

    private static AerospikeClient client = null;
    public static synchronized AerospikeClient getClient(AerospikeConfiguration conf){
        if(client != null)
            return client;
        ClientPolicy policy = new ClientPolicy();
        policy.maxConnsPerNode = 3000;
        policy.user = conf.getUsername();
        policy.password = conf.getPassword();
        client = new AerospikeClient(policy, conf.getHosts());
        return client;
    }
}
