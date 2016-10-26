package ru.mipt.java2016.homework.g000.lavrentyev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Fedor S. Lavrentyev
 * @since 25.10.16
 */
public class MapBackedStorage<K, V> implements KeyValueStorage<K, V> {
    private Map<K, V> map;

    public MapBackedStorage(Map<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map should not be null");
        }
        this.map = map;
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        map = null;
    }


    private void checkNotClosed() {
        if (map == null) {
            throw new IllegalStateException("Already closed");
        }
    }
}
