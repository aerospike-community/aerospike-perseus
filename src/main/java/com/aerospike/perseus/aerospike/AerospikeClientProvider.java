package com.aerospike.perseus.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.perseus.configurations.pojos.AerospikeConfiguration;

public class AerospikeClientProvider {

    private static AerospikeClient client = null;
    public static synchronized AerospikeClient getClient(AerospikeConfiguration conf) throws InterruptedException {
        if(client != null)
            return client;
        ClientPolicy policy = new ClientPolicy();
        policy.maxConnsPerNode = 3000;
        policy.user = conf.username;
        policy.password = conf.password;
        client = new AerospikeClient(policy, conf.getHosts());
        if(conf.truncateSet) {
            System.out.printf("Truncating the set: %s in namespace: %s!\n", conf.set, conf.namespace);
            client.truncate(null, conf.namespace, conf.set, null);
            client.truncate(null, conf.namespace, "KeyRanges", null);
            for (String indexName : new String[]{"Geo_Location", "Num_Key", "String_Key", "indexOnDate"}) {
                try {
                    client.dropIndex(null, conf.namespace, conf.set, indexName);
                } catch(Exception ignored) {}
            }
            Thread.sleep(10000);
            System.out.printf("The set: %s in namespace: %s was successfully truncated!\n", conf.set, conf.namespace);
        }

        return client;
    }
}
