package com.aerospike.perseus.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.perseus.configurations.pojos.AerospikeConfiguration;

public class AerospikeClientProvider {

    private static AerospikeClient client = null;
    public static synchronized AerospikeClient getClient(AerospikeConfiguration conf){
        if(client != null)
            return client;
        ClientPolicy policy = new ClientPolicy();
        policy.maxConnsPerNode = 3000;
        policy.user = conf.username;
        policy.password = conf.password;
        client = new AerospikeClient(policy, conf.getHosts());
        if(conf.truncateSet) {
            client.truncate(null, conf.namespace, conf.set, null);
            System.out.printf("The set: %s in namespace: %s was successfully truncated!\n", conf.set, conf.namespace);
        }

        return client;
    }
}
