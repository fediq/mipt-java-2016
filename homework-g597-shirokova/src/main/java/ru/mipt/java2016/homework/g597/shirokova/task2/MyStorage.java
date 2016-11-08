package ru.mipt.java2016.homework.g597.shirokova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private FileMap<K, V> valueStorage;

    MyStorage(String pathToFile, SerializationStrategy<K> keySerializer,
              SerializationStrategy<V> valueSerializer) throws IOException {
        valueStorage = new FileMap<>(pathToFile, keySerializer, valueSerializer);
    }

    @Override
    public V read(K key) {
        return valueStorage.read(key);
    }

    @Override
    public boolean exists(K key) {
        return valueStorage.exists(key);
    }

    @Override
    public void write(K key, V value) {
        valueStorage.write(key, value);
    }

    @Override
    public void delete(K key) {
        valueStorage.delete(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return valueStorage.readKeys();
    }

    @Override
    public int size() {
        return valueStorage.size();
    }

    @Override
    public void close() throws IOException {
        valueStorage.close();
    }
}
