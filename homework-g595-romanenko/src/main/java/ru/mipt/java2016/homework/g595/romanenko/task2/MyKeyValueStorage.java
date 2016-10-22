package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.IOException;
import java.util.Iterator;


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private SSTable<K, V> table;

    MyKeyValueStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy) {

        table = new SSTable<>(path, keySerializationStrategy, valueSerializationStrategy);
    }

    @Override
    public V read(K key) {
        V value = null;
        try {
            value = table.getValue(key);
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
        return value;
    }

    @Override
    public boolean exists(K key) {
        return table.exists(key);
    }

    @Override
    public void write(K key, V value) {
        table.addKeyValue(key, value);
    }

    @Override
    public void delete(K key) {
        table.removeKey(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public void close() throws IOException {
        table.close();
    }
}
