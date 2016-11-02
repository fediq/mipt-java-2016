package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;


public class MyHashTable<K, V> implements KeyValueStorage<K, V> {

    private RandomAccessFile file;
    private HashMap<K, V> hashMap = new HashMap<>();
    private SerializationStrategy<K> keySerializator;
    private SerializationStrategy<V> valueSerializator;
    private Boolean isOpened = false;
    private File lock;

    MyHashTable(String pathGiven, String nameGiven,
                SerializationStrategy<K> keySerializer, SerializationStrategy<V> valSerializer)
            throws IOException {
        if (isOpened) {
            throw new IOException("file has been already opened");
        }
        if (pathGiven == null) {
            throw new IllegalArgumentException("No path given");
        }
        if (nameGiven == null) {
            throw new IllegalArgumentException("No name given");
        }

        lock = new File(pathGiven + File.separator + "lock.check");
        if (!lock.createNewFile()) {
            throw new IllegalArgumentException("Already opened in other process");
        }

        String path = pathGiven + File.separator + nameGiven + ".db";
        File f = new File(path);

        keySerializator = keySerializer;
        valueSerializator = valSerializer;

        file = new RandomAccessFile(f, "rw");
        isOpened = true;
        if (f.exists()) {
            readData();
        }
    }

    @Override
    public V read(K key) {
        return hashMap.get(key);
    }

    @Override
    public boolean exists(K key) {
        return hashMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        hashMap.put(key, value);
    }

    @Override
    public void delete(K key) {
        hashMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return hashMap.keySet().iterator();
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public void close() throws IOException {
        try {
            if (!isOpened) {
                throw new IOException("file is not opened");
            }

            file.setLength(0);
            for (K key : hashMap.keySet()) {
                keySerializator.write(file, key);
                valueSerializator.write(file, hashMap.get(key));
            }
        } finally {
            hashMap.clear();
            file.close();
            lock.delete();
            isOpened = false;
        }
    }

    private void readData() throws IOException {
        file.seek(0);
        hashMap.clear();

        while (file.getFilePointer() < file.length()) {
            K key = keySerializator.read(file);
            V value = valueSerializator.read(file);
            if (hashMap.containsKey(key)) {
                throw new IOException("Key already in db");
            }
            hashMap.put(key, value);
        }
    }
}
