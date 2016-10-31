package ru.mipt.java2016.homework.g597.shirokova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private FileMap<K, V> ValueStorage;

    MyStorage(String pathToFile, SerializationStrategy<K> keySerializer,
              SerializationStrategy<V> valueSerializer) throws IOException {
        ValueStorage = new FileMap<>(pathToFile, keySerializer, valueSerializer);
    }

    @Override
    public V read(K key) {
        return ValueStorage.read(key);
    }

    @Override
    public boolean exists(K key) {
        return ValueStorage.exists(key);
    }

    @Override
    public void write(K key, V value) {
        ValueStorage.write(key, value);
    }

    @Override
    public void delete(K key) {
        ValueStorage.delete(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return ValueStorage.readKeys();
    }

    @Override
    public int size() {
        return ValueStorage.size();
    }

    @Override
    public void close() throws IOException {
        ValueStorage.close();
    }
}
