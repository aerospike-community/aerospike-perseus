package com.aerospike.perseus.configurations;
import com.aerospike.perseus.configurations.pojos.Configuration;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

public class ConfigurationProvider {
    private final Yaml yaml = new Yaml(new Constructor(Configuration.class, new LoaderOptions()));

    public Configuration getConfiguration() {
        try (InputStream inputStream = new FileInputStream(ResourceFileProvider.getConfigPath().toFile())) {
        return yaml.load(inputStream);
        } catch (Exception e) {
            return new Configuration();
        }
    }
}
