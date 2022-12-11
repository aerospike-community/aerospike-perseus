package com.aerospike.random;

import com.aerospike.raw.Data;
import com.aerospike.raw.SalesData;
import com.aerospike.raw.SimpleData;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleDataGenerator implements dataGenerator {
    private final static String [] locales = Locale.getISOCountries();

    final ThreadLocalRandom random ;

    public SimpleDataGenerator() {
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public Data next() {
        var key = random.nextInt();
        int one = 1;
        int number = random.nextInt();
        String text = new Locale("", locales[ random.nextInt( locales.length) ] ).getDisplayCountry();
        var date = between(Instant.now().minus(Period.ofWeeks(30)), Instant.now()).toString();

        byte[] array = new byte[250];
        random.nextBytes(array);
        String dummy = new String(array, Charset.forName("UTF-8"));

        return new SimpleData(key, one, number, text, date, dummy);
    }

    public Instant between(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long randomDate = random.nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(randomDate);
    }
}
