package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class ImplementationKeyValueStorage<K,V> implements KeyValueStorage<K,V> {

    private final HashMap<K,V> map = new HashMap<>();

    private boolean openFlag = true;

    @Override
    public V read(K key) {
        if (openFlag) {
            return map.get(key);
        }
        else {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (openFlag) {
            map.putIfAbsent(key, value);
        }
        else {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        for (int i = 0; i < map.size(); ++i) {

        }
    }
}
