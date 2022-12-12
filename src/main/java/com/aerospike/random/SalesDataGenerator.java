package com.aerospike.random;

import com.aerospike.raw.Data;
import com.aerospike.raw.SalesData;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SalesDataGenerator implements DataGenerator {

    private final static String[] segments = {
            "Government",
            "Retail",
            "Health",
            "Sport",
            "Education",
            "Midmarket",
            "Channel Partners",
            "Enterprise",
            "Small Business"
    };
    private final static String [] locales = Locale.getISOCountries();
    private final static String [] products = {
            "Aerospike Real-time Data Platform",
            "Aerospike Database",
            "Aerospike SQL",
            "Document Database",
            "Features and Editions",
            "Aerospike Tools",
            "Kubernetes Operator",
            "Aerospike Monitoring",
            "Cloud",
            "Aerospike Cloud",
            "Aerospike on AWS",
            "Aerospike Cloud Managed Service",
            "Connect",
            "Connect for Spark",
            "Connect for Kafka",
            "Connect for JMS",
            "Connect for Pulsar",
            "Connect for Presto",
            "Connect for ESP",
            "Technology",
            "Real-Time Engine",
            "Hybrid Memory Architecture",
            "Cross Datacenter Replication",
            "Dynamic Cluster Management",
            "Smart Client™",
            "Strong Consistency"
    };

    final ThreadLocalRandom random ;

    public SalesDataGenerator() {
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public Data next() {
        var key = random.nextInt();
        var segment = segments[random.nextInt( segments.length)];
        var country = new Locale("", locales[ random.nextInt( locales.length) ] ).getDisplayCountry();
        var product = products[random.nextInt( products.length) ];
        var unitsSold =random.nextInt( 1, 10 );
        var mfgPrice = random.nextInt( 100,300 );
        var salesPrice = random.nextInt( mfgPrice, mfgPrice*2);
        var date = between(Instant.now().minus(Period.ofWeeks(30)), Instant.now()).toString();

        byte[] array = new byte[230];
        random.nextBytes(array);
        String dummy = new String(array, Charset.forName("UTF-8"));

        return new SalesData(key, segment, country, product, unitsSold, mfgPrice, salesPrice, date, dummy);
    }

    public Instant between(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long randomDate = random.nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(randomDate);
    }
}
