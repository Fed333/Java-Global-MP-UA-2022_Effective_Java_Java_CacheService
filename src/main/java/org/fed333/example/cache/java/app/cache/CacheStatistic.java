package org.fed333.example.cache.java.app.cache;

public interface CacheStatistic {

    Long incrementEvictions();

    Double calcAveragePuttingTime(long newTime);

    Double getAveragePuttingTime();

    Long getEvictions();

}
