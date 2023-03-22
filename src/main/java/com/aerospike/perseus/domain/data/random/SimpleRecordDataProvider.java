package com.aerospike.perseus.domain.data.random;

import com.aerospike.perseus.domain.data.Record;
import com.aerospike.perseus.domain.data.SimpleRecord;
import com.aerospike.perseus.domain.data.DataProvider;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleRecordDataProvider implements DataProvider<Record> {
    private final static String [] locales = Locale.getISOCountries();
    private final int sizeOfTheObject;
    final ThreadLocalRandom random ;

    public SimpleRecordDataProvider(int sizeOfTheObject) {
        this.sizeOfTheObject = sizeOfTheObject;
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public Record next() {
        var key = random.nextInt();
        int octet = random.nextInt(16);
        int keyPlus10 = key+10;
        int keyPlus20 = key+20;
        String country = new Locale("", locales[ random.nextInt( locales.length) ] ).getDisplayCountry();
        long date = between(Instant.now().minus(Period.ofWeeks(300)), Instant.now()).getEpochSecond();

        byte[] array = new byte[sizeOfTheObject];
        random.nextBytes(array);
        String dummy = new String(array, StandardCharsets.UTF_8);

        return new SimpleRecord(key, octet, keyPlus10, keyPlus20, country, date, dummy);
    }

    public Instant between(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long randomDate = random.nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(randomDate);
    }
}
