package org.fed333.example.cache.java.app.cache.impl;

import org.fed333.example.cache.java.app.cache.CustomCache;
import org.fed333.example.cache.java.app.entity.CacheValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class JavaLFUCacheTest {

    @Autowired
    private CustomCache<String, CacheValue> cache;

    @Test
    public void cache_statisticAverageTime() {

        Long begin = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            cache.put(String.valueOf(i));
        }
        Long end = System.nanoTime();
        Double expectedAverage = (end-begin)/1000.0;

        Double actualAverageTime = cache.getStatistic().getAveragePuttingTime();

        Assertions.assertTrue(Math.abs(actualAverageTime-expectedAverage) < 10000);

    }


}