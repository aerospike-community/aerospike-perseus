package com.aerospike.perseus.testCases;

import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpReadFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.perseus.data.generators.key.KeyCache;

public class ExpressionReadTest extends Test<Long>{
    public ExpressionReadTest(TestCaseConstructorArguments arguments, KeyCache cache) {
        super(arguments, cache);
    }

    @Override
    protected void execute(Long key) {
        Expression readExp = Exp.build(
                Exp.cond(
                        Exp.eq(
                                Exp.sub(
                                        Exp.intBin(com.aerospike.perseus.data.Record.KEY_PLUS_10),
                                        Exp.intBin(com.aerospike.perseus.data.Record.NUMERIC_BIN)),
                                Exp.val(10)),
                        Exp.val("Yes"),
                        Exp.val("No")));
        Operation readExpOp = ExpOperation.read("ExpRes",
                readExp,
                ExpReadFlags.DEFAULT);

        Record operate = client.operate(null, getKey(key), readExpOp);
//        if(operate != null)
//            System.out.println(operate.getString("r"));
    }

    public String[] getHeader(){
        return "Expression\nRead".split("\n");
    }
}
