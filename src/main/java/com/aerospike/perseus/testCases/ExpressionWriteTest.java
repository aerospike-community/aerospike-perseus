package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpWriteFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.keyCache.Cache;

public class ExpressionWriteTest extends Test<Long>{

    public ExpressionWriteTest(AerospikeClient client, Cache<Long> cache, String namespace, String setName) {
        super(client, cache, namespace, setName);
    }

    @Override
    protected void execute(Long key) {
        Expression writeExp = Exp.build(
                Exp.cond(
                        Exp.eq(
                                Exp.sub(
                                        Exp.intBin(com.aerospike.perseus.data.Record.KEY_PLUS_10),
                                        Exp.intBin(com.aerospike.perseus.data.Record.NUMERIC_BIN)),
                                Exp.val(10)),
                        Exp.val("Yes"),
                        Exp.val("No")));
        Operation writeExpOp = ExpOperation.write("ExpRes",
                writeExp,
                ExpWriteFlags.DEFAULT);

        WritePolicy policy = new WritePolicy(client.writePolicyDefault);
        Record operate = client.operate(policy, getKey(key), writeExpOp);
//        if(operate != null)
//            System.out.println(operate.toString());
    }

    public String[] getHeader(){
        return "Expression\nWrite".split("\n");
    }
}
