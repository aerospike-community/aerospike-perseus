package com.aerospike.perseus.utilities.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;

public class AerospikeClientProvider {
    public static AerospikeClient getClient(AerospikeConfiguration conf){
        ClientPolicy policy = new ClientPolicy();
        policy.maxConnsPerNode = 300;
        return new AerospikeClient(policy, conf.getHosts());
    }
}
