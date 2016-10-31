package ru.mipt.java2016.homework.g595.nosareva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by maria on 25.10.16.
 */
public class KVStorage<K, V> implements KeyValueStorage<K, V> {

    private SimpleStorageTable<K, V> storageTable;

    public KVStorage(String path, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws IOException {
        storageTable = new SimpleStorageTable<>(path, keySerializer, valueSerializer);
    }

    @Override
    public V read(K key) {
        return storageTable.read(key);
    }

    @Override
    public boolean exists(K key) {
        return storageTable.exists(key);
    }

    @Override
    public void write(K key, V value) {
        storageTable.write(key, value);
    }

    @Override
    public void delete(K key) {
        storageTable.delete(key);
    }

    @Override
    public int size() {
        return storageTable.getSize();
    }

    @Override
    public Iterator<K> readKeys() {
        return storageTable.readKeys();
    }

    @Override
    public void close() throws IOException {
        storageTable.close();
    }
}
