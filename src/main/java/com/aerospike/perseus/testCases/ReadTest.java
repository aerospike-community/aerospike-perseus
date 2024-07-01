package com.aerospike.perseus.testCases;

import com.aerospike.perseus.keyCache.CacheHitAndMissKeyProvider;

public class ReadTest extends Test<Long>{
    private final double hitRatio;
    public ReadTest(TestCaseConstructorArguments arguments, CacheHitAndMissKeyProvider provider, double hitRatio) {
        super(arguments, provider);
        this.hitRatio = hitRatio;
    }

    @Override
    protected void execute(Long key) {
        client.get(null, getKey(key));
    }

    public String[] getHeader(){
        return String.format("Read\nHitRate %s%d", "%", (int)(hitRatio*100)).split("\n");
    }
}
