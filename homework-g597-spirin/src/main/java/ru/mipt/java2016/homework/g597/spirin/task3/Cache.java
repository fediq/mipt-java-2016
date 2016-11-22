package ru.mipt.java2016.homework.g597.spirin.task3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by whoami on 11/22/16.
 */

/**
 * Cache is for optimizing real-life queries for key-value storage
 * @param <K> - keys
 * @param <V> - values
 */
public class Cache<K, V> {

    private int DEFAULT_MAX_CACHE_SIZE = 100;

    private int maxCacheSize;
    private Map<K, V> cache;
    private Queue<K> queue;

    Cache() {
        this.maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
        cache = new HashMap<>();
        queue = new LinkedList<>();
    }

    Cache(int maxCacheSize) {
        if (maxCacheSize < 1) {
            throw new RuntimeException("Cache maximum size cannot be less than 1.");
        }

        this.maxCacheSize = maxCacheSize;
        cache = new HashMap<>();
        queue = new LinkedList<>();
    }

    void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Unexpected null key or value.");
        }

        if (queue.size() >= maxCacheSize) {
            // Remove the last added element
            cache.remove(queue.poll());
        }

        cache.put(key, value);
        queue.add(key);
    }

    V get(K key) {
        if (key == null) {
            throw new NullPointerException("Unexpected null key.");
        }

        return cache.get(key);
    }

}
