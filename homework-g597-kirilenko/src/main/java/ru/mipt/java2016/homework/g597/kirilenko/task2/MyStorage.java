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
    private File f = new File("checkProcesses");

    public MyStorage(String path, MySerialization<K> serializeK, MySerialization<V> serializeV) throws IOException {
        if (!f.createNewFile()) {
            throw new IOException("Error");
        }
        close = false;
        fullPath = path + File.separator + "storage";
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

    private void isClose() {
        if (close) {
            throw new IllegalStateException("Error");
        }
    }

    @Override
    public V read(K key) {
        isClose();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        isClose();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isClose();
        storage.put(key, value);
    }

    @Override
    public void delete(Object key) {
        isClose();
        storage.remove(key);
    }

    @Override
    public Iterator readKeys() {
        isClose();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        isClose();
        return storage.size();
    }

    @Override
    public void close() throws IOException {
        isClose();
        close = true;
        f.delete();
        rwFile.seek(0);
        rwFile.setLength(0);
        SerializationType.SerializationInteger.getSerialization().write(rwFile, storage.size());
        Set<K> keys = storage.keySet();
        for (K k : keys) {
            keySerialization.write(rwFile, k);
            valueSerialization.write(rwFile, storage.get(k));
        }
        rwFile.close();
    }
}
