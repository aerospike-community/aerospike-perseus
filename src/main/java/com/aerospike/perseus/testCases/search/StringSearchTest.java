package com.aerospike.perseus.testCases.search;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.Statement;
import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.keyCache.Cache;
import com.aerospike.perseus.presentation.TotalTpsCounter;
import com.aerospike.perseus.testCases.TestCaseConstructorArguments;

public class StringSearchTest extends BaseSearchTest<Long> {
    public StringSearchTest(TestCaseConstructorArguments arguments, Cache keyCache) {
        super(arguments, keyCache);
        client.createIndex(null,
                namespace,
                setName,
                "String_Key",
                Record.STRING_BIN,
                IndexType.STRING).waitTillComplete();
        System.out.println("String Index was created successfully.");
    }

    @Override
    protected Statement getStatement(Long value)  {
        Statement statement = new Statement();
        statement.setFilter(Filter.equal(Record.STRING_BIN, value.toString()));
        statement.setNamespace(namespace);
        statement.setSetName(setName);
        return statement;
    }

    @Override
    public String[] getHeader() {
        return "String\nSearch".split("\n");
    }
}
