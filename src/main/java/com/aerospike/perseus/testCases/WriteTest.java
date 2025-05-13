package com.aerospike.perseus.testCases;

import com.aerospike.perseus.data.Record;
import com.aerospike.perseus.data.generators.RecordGenerator;

public class WriteTest extends Test<Record>{

    public WriteTest(TestCaseConstructorArguments arguments, RecordGenerator provider) {
        super(arguments, provider);
    }

    @Override
    protected void execute(Record record) {
        com.aerospike.client.Key key = getKey(record.getKey());
        client.put(null, key, record.getBins());
    }

    public String[] getHeader(){
        return "Write\n ".split("\n");
    }
}
