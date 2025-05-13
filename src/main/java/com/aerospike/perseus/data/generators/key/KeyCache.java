package com.aerospike.perseus.data.generators.key;

import com.aerospike.client.*;
import com.aerospike.client.Record;
import com.aerospike.client.cdt.MapOperation;
import com.aerospike.client.cdt.MapOrder;
import com.aerospike.client.cdt.MapPolicy;
import com.aerospike.client.cdt.MapWriteMode;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class KeyCache implements Iterator<Long> {

    private final AerospikeClient client;
    private final String perseusId;

    private final Key key;
    private final long defaultStart;

    private Map<String, Pair<Long, AtomicLong>> ranges;
    private final ThreadLocalRandom random;
    private long lengthOfRange;

    public KeyCache(AerospikeClient client, String perseusId, long defaultStart, String namespace) {
        this.client = client;
        this.perseusId = perseusId;
        this.defaultStart = defaultStart;
        this.random = ThreadLocalRandom.current();
        key = new Key(namespace, "KeyRanges", "ID");
        updateRanges();

        var scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                this::updateRanges, 2, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public final Long next() {
        long r = random.nextLong(lengthOfRange + getCurrent().get() - getCurrentStart() + 1);
        long currentRange = 0;
        for (String key: ranges.keySet()) {
            if( (r-currentRange) < (ranges.get(key).second().get() - ranges.get(key).first()))
                return  ranges.get(key).first() + (r - currentRange);
            currentRange += (ranges.get(key).second().get() - ranges.get(key).first());
        }
        return random.nextLong();
    }

    private long getCurrentStart() {
        return ranges.get(perseusId).first();
    }

    public AtomicLong getCurrent() {
        return ranges.get(perseusId).second();
    }

    public void updateRanges() {
        String BIN_NAME = "Ranges";
        try {
            Record record = client.get(null, key);
            if (record == null) {
                ranges = new HashMap<>();
                ranges.put(perseusId, new Pair<>(defaultStart, new AtomicLong(defaultStart)));
                Pair<Long, AtomicLong> currentRange = ranges.get(perseusId);
                var list = Arrays.asList(currentRange.first(), currentRange.second().get());
                var map = new HashMap<>();
                map.put(perseusId, new Value.ListValue(list));
                var bin = new Bin(BIN_NAME, new Value.MapValue(map));
                client.put(null, key, bin);
                return;
            }

            var map = (Map<String, List<Long>>) record.getMap(BIN_NAME);
            if (map == null) {
                ranges = new HashMap<>();
                ranges.put(perseusId, new Pair<>(defaultStart, new AtomicLong(defaultStart)));
            } else {
                if (ranges == null) {
                    ranges = new HashMap<>();
                    var range = map.get(perseusId);
                    if (range != null) {
                        ranges.put(perseusId, new Pair<>(range.get(0), new AtomicLong(range.get(1))));
                    } else {
                        ranges.put(perseusId, new Pair<>(defaultStart, new AtomicLong(defaultStart)));
                    }
                }

                for (String id : map.keySet()) {
                    if (id.equals(perseusId))
                        continue;

                    List<Long> list = map.get(id);
                    ranges.put(id, new Pair<>(list.get(0), new AtomicLong(list.get(1))));
                }
            }

            calculateSizeOfTheRange();

            MapPolicy mp = new MapPolicy(MapOrder.UNORDERED, MapWriteMode.UPDATE);
            Pair<Long, AtomicLong> currentRange = ranges.get(perseusId);
            var list = Arrays.asList(currentRange.first(), currentRange.second().get());
            client.operate(null, key, MapOperation.put(mp, BIN_NAME, Value.get(perseusId), new Value.ListValue(list)));
        } catch (AerospikeException e) {
            System.out.println(e.getBaseMessage());
        }
    }

    private void calculateSizeOfTheRange() {
        lengthOfRange = 0;
        for (String key : ranges.keySet()) {
            if(key.equals(perseusId))
                continue;
            lengthOfRange += (ranges.get(key).second().get() - ranges.get(key).first());
        }
    }
}
