package org.fed333.example.cache.java.app.cache.impl;

import org.fed333.example.cache.java.app.cache.CustomCache;
import org.fed333.example.cache.java.app.entity.CacheValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
class JavaLFUCacheTest {

    @Autowired
    private CustomCache<String, CacheValue> cache;

    private static final Logger mockedLogger = Mockito.mock(Logger.class);


    @BeforeAll
    public static void setUpAll() {
        MockedStatic<LoggerFactory> loggerFactoryMockedStatic = Mockito.mockStatic(LoggerFactory.class);
        loggerFactoryMockedStatic.when(() -> LoggerFactory.getLogger(any(String.class))).thenReturn(mockedLogger);
        loggerFactoryMockedStatic.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockedLogger);

    }

    @Test
    public void cache_MaxSizeIs100_000() {
        fillCacheWithTestData(200_000, "String ");

        Assertions.assertEquals(100_000, cache.getSize());
    }

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

    @Test
    public void cache_isLeastFrequentlyUsedRemoved() {
        cache.setCapacity(5);
        fillCacheWithTestData(5, "");
        for (int i = 0; i < 10; i++) {
            cache.get("0");
        }
        for (int i = 0; i < 8; i++) {
            cache.get("1");
        }
        for (int i = 0; i < 6; i++) {
            cache.get("2");
        }
        cache.get("3");
        cache.get("4");
        cache.get("2");
        cache.get("1");

        cache.put("5");
        cache.get("5");
        cache.put("6");

        Assertions.assertNotNull(cache.get("0"));
        Assertions.assertNotNull(cache.get("1"));
        Assertions.assertNull(cache.get("3"));
        Assertions.assertNull(cache.get("4"));

    }

    @Test
    public void cache_RemoveEntryLog() {
        cache.setCapacity(1);
        String expectedRemovedKey = "Remove entry1";
        CacheValue expectedRemovedValue = new CacheValue(expectedRemovedKey);

        cache.put(expectedRemovedKey);
        cache.put("Remove entry2");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CacheValue> valueCaptor = ArgumentCaptor.forClass(CacheValue.class);

        verify(mockedLogger).info(messageCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        Assertions.assertEquals("Element: [key={}, value={}] has been evicted.", messageCaptor.getValue());
        Assertions.assertEquals(expectedRemovedKey, keyCaptor.getValue());
        Assertions.assertEquals(expectedRemovedValue, valueCaptor.getValue());

    }

    @Test
    public void cache_evictionsNumber() {
        cache.setCapacity(10);
        for (int i = 0; i < 100; i++) {
            cache.put(String.valueOf(i));
        }

        Assertions.assertEquals(90, cache.getStatistic().getEvictions());

    }

    private void fillCacheWithTestData(int size, String prefix) {
        for (int i = 0; i < size; i++) {
            cache.put(prefix + i);
        }
    }

}