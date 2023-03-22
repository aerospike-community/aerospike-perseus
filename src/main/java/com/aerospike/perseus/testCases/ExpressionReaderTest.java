package com.aerospike.perseus.testCases;

import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpReadFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class ExpressionReaderTest extends Test<Integer>{
    public ExpressionReaderTest(AerospikeConfiguration conf, KeyProvider<Integer> provider) {
        super(conf, provider);
    }

    @Override
    protected void execute(Integer key) {
        Expression readExp = Exp.build(
                Exp.cond(
                        Exp.gt(
                                Exp.mul(
                                        Exp.sub(
                                                Exp.intBin("keyPlus20"),
                                                Exp.intBin("keyPlus10")),
                                        Exp.intBin("octet")),
                                Exp.val(100)),
                        Exp.val("Yes"),
                        Exp.val("No")));
        Operation readExpOp = ExpOperation.read("EXPRes",
                readExp,
                ExpReadFlags.DEFAULT);

        Record operate = client.operate(null, getKey(key), readExpOp);
//        if(operate != null)
//            System.out.println(operate.getString("r"));
    }

    public String getHeader(){
        return "Expression R";
    }
}
