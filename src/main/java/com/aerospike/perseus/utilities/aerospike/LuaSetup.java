package com.aerospike.perseus.utilities.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.lua.LuaConfig;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.perseus.utilities.ConfigFileProvider;

public class LuaSetup {
    public static void registerUDF(AerospikeConfiguration conf, String luaFileName) {

        var configFileProvider = new ConfigFileProvider(luaFileName, "example.lua");

        LuaCache.clearPackages();
        LuaConfig.SourceDirectory = configFileProvider.getPath().getParent().toString();
        AerospikeClient client = AerospikeClientProvider.getClient(conf);
        client.removeUdf(null, luaFileName);

        RegisterTask task = client.register(null,
                configFileProvider.getPath().toString(),
                configFileProvider.getPath().getFileName().toString(),
                Language.LUA);
        task.waitTillComplete();
    }
}
