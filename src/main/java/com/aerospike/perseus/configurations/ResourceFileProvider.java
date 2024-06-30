package com.aerospike.perseus.configurations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ResourceFileProvider {
    private final static String configPathName = "configuration.yaml";
    private final static String threadsPath = "threads.yaml";
    private final static String luaPath = "udf_aggregation.lua";
    private final static String udfPath = "same_as_expression.lua";

    private static Path copy(String resourceName) throws IOException {
        var localPath = Paths.get(resourceName).toAbsolutePath();
        if(Files.exists(localPath))
            return localPath;

        InputStream resource = ConfigurationProvider.class.getClassLoader().getResourceAsStream(resourceName);
        if(!Files.exists(localPath.getParent()))
            Files.createDirectory(localPath.getParent());
        Files.copy(resource, localPath, StandardCopyOption.REPLACE_EXISTING);
        return localPath;
    }

    public static Path getConfigPath() throws IOException {
        return copy(configPathName);
    }

    public static Path getThreadsPath() throws IOException {
        return copy(threadsPath);
    }

    public static Path getUdfAggregationPath() throws IOException {
        return copy(luaPath);
    }

    public static Path getUdfPath() throws IOException {
        return copy(udfPath);
    }
}
