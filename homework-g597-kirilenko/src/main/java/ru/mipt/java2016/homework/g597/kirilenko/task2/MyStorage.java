package ru.mipt.java2016.homework.g597.kirilenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization.SerializationType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by Natak on 27.10.2016.
 */



public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    private static boolean close = false;
    private String fullPath;
    private RandomAccessFile rwFile;
    private HashMap<K, V> storage = new HashMap<K, V>();
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    public MyStorage(String path, MySerialization<K> serializeK, MySerialization<V> serializeV) throws IOException {
        close = false;
        fullPath = path + "/" + "storage";
        keySerialization = serializeK;
        valueSerialization = serializeV;
        File file = new File(fullPath);
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("There is no such directory");
        }
        if (file.exists()) {
            rwFile = new RandomAccessFile(file, "rw");
            int size = SerializationType.SerializationInteger.getSerialization().read(rwFile);
            for (int i = 0; i < size; ++i) {
                K key = keySerialization.read(rwFile);
                V value = valueSerialization.read(rwFile);
                storage.put(key, value);
            }
        } else {
            file.createNewFile();
            rwFile = new RandomAccessFile(file, "rw");
        }
    }

    @Override
    public V read(K key) {
        if (!close) {
            return storage.get(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public boolean exists(K key) {
        if (!close) {
            return storage.containsKey(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void write(K key, V value) {
        if (!close) {
            storage.put(key, value);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void delete(Object key) {
        if (!close) {
            storage.remove(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public Iterator readKeys() {
        if (!close) {
            return storage.keySet().iterator();
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public int size() {
        if (!close) {
            return storage.size();
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void close() throws IOException {
        close = true;
        rwFile.seek(0);
        SerializationType.SerializationInteger.getSerialization().write(rwFile, storage.size());
        Set<K> keys = storage.keySet();
        for (K k : keys) {
            keySerialization.write(rwFile, k);
            valueSerialization.write(rwFile, storage.get(k));
        }
        rwFile.close();
    }
}
