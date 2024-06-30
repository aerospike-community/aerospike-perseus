package com.aerospike.perseus;

import com.aerospike.perseus.testCases.*;
import com.aerospike.perseus.configurations.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        var config = new ConfigurationProvider().getConfiguration();

        var setup = new TestSetup(config.aerospikeConfiguration, config.testConfiguration);
        new OutputWindow(config.outputWindowConfiguration, setup.getLoggableTestList(), setup.getCacheStats());
        setup.startTest();


    }

}