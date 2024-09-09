package com.aerospike.perseus.testCases;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpWriteFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.keyCache.Cache;
import com.aerospike.perseus.presentation.TotalTpsCounter;

public class ExpressionWriteTest extends Test<Long>{

    public ExpressionWriteTest(TestCaseConstructorArguments arguments, Cache<Long> cache) {
        super(arguments, cache);
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
                ExpWriteFlags.EVAL_NO_FAIL | ExpWriteFlags.POLICY_NO_FAIL);
        try {
            Record operate = client.operate(null, getKey(key), writeExpOp);
            //        if(operate != null)
            //            System.out.println(operate.toString());
        } catch (AerospikeException ignore) {
            // if the data gets deleted, the expression may not be able to find the record and hence it may throw an exception. 
        }
    }

    public String[] getHeader(){
        return "Expression\nWrite".split("\n");
    }
}
