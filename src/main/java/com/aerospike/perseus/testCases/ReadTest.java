package com.aerospike.perseus.testCases;

import com.aerospike.perseus.data.generators.key.ProbabilisticKeyCache;

public class ReadTest extends Test<Long>{
    private final double hitRatio;
    public ReadTest(TestCaseConstructorArguments arguments, ProbabilisticKeyCache cache, double hitRatio) {
        super(arguments, cache);
        this.hitRatio = hitRatio;
    }

    @Override
    protected void execute(Long key) {
        client.get(null, getKey(key));
    }

    public String[] getHeader(){
        if(hitRatio == 1){
            return "Read\n ".split("\n");
        }
        return String.format("Read\nHitRate %s%d", "%", (int)(hitRatio*100)).split("\n");
    }
}
