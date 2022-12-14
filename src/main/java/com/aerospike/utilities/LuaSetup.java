package com.aerospike.utilities;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.lua.LuaCache;
import com.aerospike.client.lua.LuaConfig;
import com.aerospike.client.task.RegisterTask;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LuaSetup {
    public static void registerUDF(AerospikeClient client, String luaDirectory, String luaFileName) {
        InputStream luaStream = LuaSetup.class.getClassLoader().getResourceAsStream(luaFileName);
        String fullPath = luaDirectory + "/" + luaFileName;
        try {
            Path dir = Paths.get(luaDirectory);
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }

            java.nio.file.Files.copy(
                    luaStream,
                    Paths.get(fullPath),
                    StandardCopyOption.REPLACE_EXISTING);

            luaStream.close();
        }
        catch (Exception e) {
            System.out.format("Failed to create Lua module %s, exception: %s.", fullPath, e);
        }

        LuaCache.clearPackages();
        LuaConfig.SourceDirectory = luaDirectory;
        client.removeUdf(null, luaFileName);

        RegisterTask task = client.register(null, fullPath, luaFileName, Language.LUA);
        task.waitTillComplete();
    }
}
