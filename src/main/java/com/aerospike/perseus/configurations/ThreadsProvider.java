package com.aerospike.perseus.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.HashMap;
import java.util.Map;

public class ThreadsProvider {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public Map<String, Integer> getThreads() {
        try{
            return mapper.readValue(ResourceFileProvider.getThreadsPath().toFile(), Map.class);
        } catch (Exception e) {
            return new HashMap<String, Integer>();
        }
    }
}
