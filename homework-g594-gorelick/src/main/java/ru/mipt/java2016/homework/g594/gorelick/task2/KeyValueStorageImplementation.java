package ru.mipt.java2016.homework.g594.gorelick.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class KeyValueStorageImplementation<K, V> implements KeyValueStorage<K, V> {
    private HashMap<K, V> kvHashMap = new HashMap<>();
    private RandomAccessFileManager RAFManager;
    private boolean closed;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private String filePath;
    
    public KeyValueStorageImplementation(String path, Serializer<K> key_serializer, Serializer<V> value_serializer) throws IOException {
        RAFManager = new RandomAccessFileManager(path, "database.db");
        filePath = path + "database.db";
        keySerializer = key_serializer;
        valueSerializer = value_serializer;
        K key;
        V value;
        while (true) {
            try {
                key = keySerializer.deserialize(RAFManager.read());
            } catch (EOFException exception) {
                break;
            }
            try {
                value = valueSerializer.deserialize(RAFManager.read());
            } catch (EOFException exception) {
                break;
            }
            kvHashMap.put(key, value);
        }
        closed = false;
    }

    @Override
    public Iterator<K> readKeys() {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        return kvHashMap.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        return kvHashMap.containsKey(key);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        closed = true;
        RAFManager.clearRAF();
        for (Map.Entry<K, V> pair: kvHashMap.entrySet()) {
            keySerializer.serialize(pair.getKey());
            valueSerializer.serialize(pair.getValue());
        }
        kvHashMap.clear();
        RAFManager.closeRAF();
        RAFManager.clearRAF();
    }

    @Override
    public int size() {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        return kvHashMap.size();
    }

    @Override
    public void delete(K key) {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        kvHashMap.remove(key);
    }

    @Override
    public void write(K key, V value) {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        kvHashMap.put(key, value);
    }

    @Override
    public V read(K key) {
        if (closed) {
            throw new RuntimeException("Storage is closed");
        }
        return kvHashMap.get(key);
    }
}