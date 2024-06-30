package com.aerospike.perseus.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.lua.LuaConfig;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.perseus.configurations.ResourceFileProvider;

import java.io.IOException;
import java.nio.file.Path;

public class LuaSetup {
    public static void registerUDF(AerospikeClient client, Path udfPath) {
        LuaCache.clearPackages();
        LuaConfig.SourceDirectory = udfPath.getParent().toString();
        client.removeUdf(null, udfPath.getFileName().toString());

        RegisterTask task = client.register(null,
                udfPath.toString(),
                udfPath.getFileName().toString(),
                Language.LUA);
        task.waitTillComplete();
    }
}
