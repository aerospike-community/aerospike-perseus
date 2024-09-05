package com.aerospike.perseus;

import com.aerospike.perseus.presentation.OutputWindow;
import com.aerospike.perseus.configurations.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        var config = new ConfigurationProvider().getConfiguration();

        var setup = new TestSetup(config.aerospikeConfiguration, config.testConfiguration);
        new OutputWindow(config.outputWindowConfiguration,setup.getCacheStats(), setup.getLoggableTestList(), setup.getTotalTps());
        setup.startTest();


    }

}