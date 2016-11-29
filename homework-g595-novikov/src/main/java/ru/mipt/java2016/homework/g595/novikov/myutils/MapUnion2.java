package ru.mipt.java2016.homework.g595.novikov.myutils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by igor on 11/28/16.
 */
public class MapUnion2<K, V> implements Map<K, V> {
    private Map<K, V> map1;
    private Map<K, V> map2;

    public MapUnion2(Map<K, V> myMap1, Map<K, V> myMap2) {
        map1 = myMap1;
        map2 = myMap2;
    }

    @Override
    public int size() {
        return map1.size() + map2.size();
    }

    @Override
    public boolean isEmpty() {
        return map1.isEmpty() && map2.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map1.containsKey(key) || map2.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map1.containsValue(value) || map2.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (map1.containsKey(key)) {
            return map1.get(key);
        }
        return map2.get(key);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("put");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("putAll");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    // operations below are just unimplemented
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("clear");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("values");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("entrySet");
    }
}
