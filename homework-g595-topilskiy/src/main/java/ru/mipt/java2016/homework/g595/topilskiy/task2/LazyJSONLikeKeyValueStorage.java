package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

/**
 * A KeyValueStorage implementation that uses
 * - a JSON-like format for persistent Storage
 * - a HashMap for buffered use of the Storage
 *
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class LazyJSONLikeKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    public LazyJSONLikeKeyValueStorage() {
    }

    @Override
    public V read(K key) {
        return null;
    }

    @Override
    public boolean exists(K key) {
        return false;
    }

    @Override
    public void write(K key, V value) {
    }

    @Override
    public void delete(K key) {
    }

    @Override
    public Iterator<K> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void close() throws IOException {
    }
}
