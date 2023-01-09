package com.aerospike.perseus.testCases;

import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpWriteFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.data.provider.DataProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

public class ExpressionWriterTest extends Test<Integer>{

    public ExpressionWriterTest(AerospikeConfiguration conf, DataProvider<Integer> provider) {
        super(conf, provider);
    }

    @Override
    protected void execute(Integer key) {
        Expression writeExp = Exp.build(
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
        Operation writeExpOp = ExpOperation.write("EXPRes",
                writeExp,
                ExpWriteFlags.DEFAULT);

        WritePolicy policy = new WritePolicy(client.writePolicyDefault);
        Record operate = client.operate(policy, getKey(key), writeExpOp);
//        if(operate != null)
//            System.out.println(operate.toString());
    }

    public String getHeader(){
        return String.format("Exp Writes (%d)", threadCount.get());
    }
}
