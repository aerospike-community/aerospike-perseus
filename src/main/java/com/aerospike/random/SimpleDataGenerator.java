package com.aerospike.random;

import com.aerospike.raw.Data;
import com.aerospike.raw.SimpleData;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleDataGenerator implements DataGenerator {
    private final static String [] locales = Locale.getISOCountries();

    final ThreadLocalRandom random ;

    public SimpleDataGenerator() {
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public Data next() {
        var key = random.nextInt();
        int octet = random.nextInt(16);
        int keyPlus10 = key+10;
        int keyPlus20 = key+20;
        String country = new Locale("", locales[ random.nextInt( locales.length) ] ).getDisplayCountry();
        long date = between(Instant.now().minus(Period.ofWeeks(300)), Instant.now()).getEpochSecond();

        byte[] array = new byte[1100];
        random.nextBytes(array);
        String dummy = new String(array, Charset.forName("UTF-8"));

        return new SimpleData(key, octet, keyPlus10, keyPlus20, country, date, dummy);
    }

    public Instant between(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long randomDate = random.nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(randomDate);
    }
}
