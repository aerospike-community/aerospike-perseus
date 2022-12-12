package com.aerospike.utilities;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class CountrySampleProvider implements SampleProvider<String>{
    private final static String [] locales = Locale.getISOCountries();

    private final int missRatio;
    final ThreadLocalRandom random ;


    public CountrySampleProvider(int missRatio) {
        this.missRatio = missRatio;
        this.random = ThreadLocalRandom.current();
    }


    @Override
    public String getSample() {
        if(random.nextInt() % missRatio == 0)
            return locales[random.nextInt(locales.length)];

        return "BoBo Land";
    }
}
