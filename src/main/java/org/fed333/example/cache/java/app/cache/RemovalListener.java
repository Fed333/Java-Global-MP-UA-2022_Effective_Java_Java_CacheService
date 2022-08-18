package org.fed333.example.cache.java.app.cache;

import java.util.AbstractMap;

public interface RemovalListener<K, V> {

    void onRemoval(AbstractMap.SimpleImmutableEntry<K,V> entry);

}
