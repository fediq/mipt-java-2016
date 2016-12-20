package ru.mipt.java2016.homework.g595.kireev.task3;

import java.util.HashMap;

/**
 * Created by sun on 18.12.16.
 */
public class MyCache64<V, K> {
    private HashMap<K, V> fast = new HashMap<K, V>();
    private int chacheSize = 1;
    private K myKey;
    private V myValue;

    public boolean containsKey(K key) {
        return myKey == key;
    }

    public V get(K key) {
        if (containsKey(key)) {
            return myValue;
        } else {
            return null;
        }
    }

    public void put(K key, V value) {
        myValue = value;
        myKey = key;
    }

    public void erase(K key) {
        if (containsKey(key)) {
            myKey = null;
            myValue = null;
        }
    }

    public void clear() {
        erase(myKey);
    }
}