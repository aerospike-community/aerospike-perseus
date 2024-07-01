package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.perseus.presentation.TotalTpsCounter;

public record TestCaseConstructorArguments(
    AerospikeClient client,
    String namespace,
    String setName,
    TotalTpsCounter totalTpsCounter) {
}
