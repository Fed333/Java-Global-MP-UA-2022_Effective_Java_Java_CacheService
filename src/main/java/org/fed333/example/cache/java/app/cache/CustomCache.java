package org.fed333.example.cache.java.app.cache;

public interface CustomCache<K,V> {

    V get(K key);

    void put(K key);

    Long getSize();

    CacheStatistic getStatistic();

}
