package com.aerospike.perseus.testCases;

import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.ExpOperation;
import com.aerospike.client.exp.ExpWriteFlags;
import com.aerospike.client.exp.Expression;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.perseus.domain.key.KeyProvider;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;

public class ExpressionWriterTest extends Test<Integer>{

    public ExpressionWriterTest(AerospikeConfiguration conf, KeyProvider<Integer> provider) {
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

    public List<String> getHeader(){
        return List.of("Expression W", String.format("%d", threadCount.get()));
    }
}
