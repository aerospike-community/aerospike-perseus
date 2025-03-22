package com.aerospike.perseus.testCases.search;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.keyCache.Cache;
import com.aerospike.perseus.keyCache.KeyCache;
import com.aerospike.perseus.presentation.TotalTpsCounter;
import com.aerospike.perseus.testCases.TestCaseConstructorArguments;

public class NumericSearchTest extends BaseSearchTest<Long> {
    public NumericSearchTest(TestCaseConstructorArguments arguments, KeyCache keyCache) {
        super(arguments, keyCache);
        try {
            client.createIndex(null,
                    namespace,
                    setName,
                    "Num_Key",
                    Record.NUMERIC_BIN,
                    IndexType.NUMERIC).waitTillComplete();
            System.out.println("Numeric Index was created successfully.");
        } catch (Exception e) {
            System.out.println("Numeric index creation is still in progress, but the test can continue for now. However this index is not usable yet.");
        }
    }

    @Override
    protected Statement getStatement(Long value) {
        Statement statement = new Statement();
        statement.setFilter(Filter.equal(Record.NUMERIC_BIN, value));
        statement.setNamespace(namespace);
        statement.setSetName(setName);
        return statement;
    }

    @Override
    public String[] getHeader() {
        return "Numeric\nSearch".split("\n");
    }
}
