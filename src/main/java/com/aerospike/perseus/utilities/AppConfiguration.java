package com.aerospike.perseus.utilities;

import com.aerospike.client.Host;
import com.aerospike.perseus.utilities.aerospike.AerospikeConfiguration;

import java.util.List;
import java.util.Map;

public class AppConfiguration implements AerospikeConfiguration {
    private final Map<String, Object> yaml;

    public AppConfiguration(String fileName) {
        var configFileProvider = new ConfigFileProvider(fileName, "configuration.yaml");
        yaml = configFileProvider.ReadYaml();
    }

    public Host[] getHosts(){
        return ((List<Map<String,Object>>)yaml.get("Hosts"))
                .stream()
                .map(l -> new Host((String) l.get("IP"), (Integer) l.get("Port")))
                .toList()
                .toArray(new Host[0]);
    }

    public String getNamespace() {
        return (String) yaml.get("Namespace");
    }

    public String getSetName() {
        return (String) yaml.get("SetName");
    }

    public int getAutoDiscardingKeyCapacity() {
        return (int) yaml.get("AutoDiscardingKeyCapacity");
    }

    public double getAutoDiscardingKeyRatio() {
        return (double) yaml.get("AutoDiscardingKeyRatio");
    }

    public int getPrintDelay() {
        return (int) yaml.get("PrintIntervalSec");
    }

    public int getHeaderBreak() {
        return (int) yaml.get("NumberOfLinesToReprintHeader");
    }

    public String getThreadYamlFilePath() {
        return (String) yaml.get("ThreadYamlFilePath");
    }

    public String getLuaFilePath() {
        return (String) yaml.get("LuaFilePath");
    }

    public int getSizeOfTheDummyPartOfTheRecord() {
        return (int) yaml.get("SizeOfTheDummyPartOfTheRecord");
    }
}
