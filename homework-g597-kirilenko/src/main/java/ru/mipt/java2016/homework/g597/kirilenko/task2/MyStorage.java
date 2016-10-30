package ru.mipt.java2016.homework.g597.kirilenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization.SerializationInteger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by Natak on 27.10.2016.
 */



public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    static boolean close = false;
    String full_path;
    RandomAccessFile rw_file;
    Map <K, V> storage;
    private MySerialization<K> key_serialization;
    private MySerialization<V> value_serialization;
    public MyStorage(String path, MySerialization<K> serializeKey, MySerialization<V> serializeValue) throws IOException {
        full_path = path + "/" + "storage";
        key_serialization = serializeKey;
        value_serialization = serializeValue;
        File file = new File(full_path);
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("There is no such directory");
        }
        if (!file.createNewFile()) {
            rw_file = new RandomAccessFile(file, "rw");
            int size = SerializationInteger.getSerialization().read(rw_file);
            for (int i = 0; i < size; ++i) {
                K key = key_serialization.read(rw_file);
                V value = value_serialization.read(rw_file);
                storage.put(key, value);
            }
        } else {
            rw_file = new RandomAccessFile(file, "rw");
        }
    }

    @Override
    public V read(K key) {
        if (close == false) {
            return storage.get(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public boolean exists(K key) {
        if (close == false) {
            return storage.containsKey(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void write(K key, V value) {
        if (close == false) {
            storage.put(key, value);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void delete(Object key) {
        if (close == false) {
            storage.remove(key);
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public Iterator readKeys() {
        if (close == false) {
            return storage.keySet().iterator();
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public int size() {
        if (close == false) {
            return storage.size();
        } else {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void close() throws IOException {
        if (rw_file == null)
            return;

        close = true;
        rw_file.seek(0);
        SerializationInteger.getSerialization().write(rw_file, storage.size());
        Set<K> keys = storage.keySet();
        for (K k : keys) {
            key_serialization.write(rw_file, k);
            value_serialization.write(rw_file, storage.get(k));
        }
        rw_file.close();
    }
}
