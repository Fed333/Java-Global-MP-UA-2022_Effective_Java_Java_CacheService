package org.fed333.example.cache.java.app.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.fed333.example.cache.java.app.cache.CacheStatistic;
import org.fed333.example.cache.java.app.cache.CustomCache;
import org.fed333.example.cache.java.app.entity.CacheValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class JavaLFUCache implements CustomCache<String, CacheValue> {

    private HashMap<String, CacheValue> cache;//cache K and V

    private HashMap<String, Integer> keyFrequency;//K and counters

    private HashMap<Integer, LinkedHashSet<String>> frequencyKeys;//Counter and item list

    private int capacity;

    private int min = -1;

    @Autowired
    private CacheStatistic statistic;

    @PostConstruct
    private void init() {
        capacity = 100_000;
        cache = new HashMap<>();
        keyFrequency = new HashMap<>();
        frequencyKeys = new HashMap<>();
        frequencyKeys.put(1, new LinkedHashSet<>());
    }

    @Override
    public CacheValue get(String key) {
        return getCacheValue(key);
    }

    @Override
    public void put(String key) {
        Instant begin = Instant.ofEpochSecond(0, System.nanoTime());
        putKey(key);
        Instant end = Instant.ofEpochSecond(0, System.nanoTime());
        statistic.calcAveragePuttingTime(Duration.between(begin, end).getNano());
    }

    @Override
    public Long getSize() {
        return (long) cache.size();
    }

    @Override
    public CacheStatistic getStatistic() {
        return statistic;
    }

    private CacheValue load(String key) {
        return new CacheValue(key);
    }

    private CacheValue getCacheValue(String key) {
        if (!cache.containsKey(key))
            return null;
        // Get the count from counts map
        int count = keyFrequency.get(key);
        // increase the counter
        keyFrequency.put(key, count + 1);
        // remove the element from the counter to linkedhashset
        frequencyKeys.get(count).remove(key);

        // when current min does not have any data, next one would be the min
        if (count == min && frequencyKeys.get(count).size() == 0)
            min++;
        if (!frequencyKeys.containsKey(count + 1))
            frequencyKeys.put(count + 1, new LinkedHashSet<>());
        frequencyKeys.get(count + 1).add(key);
        return cache.get(key);
    }

    private void putKey(String key) {
        if (capacity <= 0)
            return;
        // If key does exist, we are returning from here
        if (cache.containsKey(key)) {
            cache.put(key, load(key));
            get(key);
            return;
        }
        if (cache.size() >= capacity) {
            String evit = frequencyKeys.get(min).iterator().next();
            frequencyKeys.get(min).remove(evit);
            cache.remove(evit);
            keyFrequency.remove(evit);
        }
        // If the key is new, insert the value and current min should be 1 of course
        cache.put(key, load(key));
        keyFrequency.put(key, 1);
        min = 1;
        frequencyKeys.get(1).add(key);
    }

}
