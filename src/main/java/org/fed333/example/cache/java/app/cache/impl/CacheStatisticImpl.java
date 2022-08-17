package org.fed333.example.cache.java.app.cache.impl;


import org.fed333.example.cache.java.app.cache.CacheStatistic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CacheStatisticImpl implements CacheStatistic {

    private Double oldAverage = 0d;

    private Long puttingNumber = 0L;

    private Long cacheEvictions = 0L;

    /**
     * Increments evictions by one.
     * @return old value
     * */
    public Long incrementEvictions() {
        return cacheEvictions++;
    }

    /**
     * Calculates average putting time based on previous times.
     * @param newTime in nanoseconds
     * @return average putting time in nanoseconds
     * */
    public Double calcAveragePuttingTime(long newTime) {
        long newPuttingNumber = puttingNumber + 1;
        double newAveragePuttingTime = ((oldAverage / newPuttingNumber) * puttingNumber + ((double)newTime / newPuttingNumber));
        oldAverage = newAveragePuttingTime;
        puttingNumber = newPuttingNumber;
        return newAveragePuttingTime;
    }


    @Override
    public Double getAveragePuttingTime() {
        return oldAverage;
    }

    @Override
    public Long getEvictions() {
        return cacheEvictions;
    }
}
