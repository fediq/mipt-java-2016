package ru.mipt.java2016.homework.g595.ferenets.task3;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ferenets.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyOptimizedStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int MAX_MEM_SIZE = 100;
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;
    private HashMap<K, V> map;
    private HashMap<K, Long> keyValueOffset;
    private boolean opened;
    private RandomAccessFile storage;
    private RandomAccessFile offsets;



    MyOptimizedStorage(String path, SerializationStrategy<K> argKeySerialization,
                       SerializationStrategy<V> argValueSerialization) throws IOException {
        keySerialization = argKeySerialization;
        valueSerialization = argValueSerialization;
        File storageFile = new File(path + File.separator + "storage.txt");
        File offsetsFile = new File(path + File.separator + "offsets.txt");
        try {
            offsets = new RandomAccessFile(offsetsFile, "rw");
            storage = new RandomAccessFile(storageFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int offsetsSize = offsets.readInt();
        for (int i = 0; i < offsetsSize; i++) {
            try {
                K key = keySerialization.read(offsets);
                long offset = offsets.readLong();
                keyValueOffset.put(key, offset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void checkFileAccess() {
        if (!opened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    private void pushMapIntoFile() throws IOException {
        if (map.size() < MAX_MEM_SIZE) {
            return;
        }
        storage.seek(storage.length());
        for (Map.Entry<K, V> entry: map.entrySet()) {
            if (!keyValueOffset.containsKey(entry.getKey())) {
                keyValueOffset.put(entry.getKey(), storage.getFilePointer());
                valueSerialization.write(storage, entry.getValue());
            }
        }
        map.clear();
    }

    @Override
    public V read(K key) {
        checkFileAccess();
        try {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            if (keyValueOffset.containsKey(key)) {
                long offset = keyValueOffset.get(key);
                storage.seek(offset);
                V value = valueSerialization.read(storage);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean exists(K key) {
        checkFileAccess();
        return keyValueOffset.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkFileAccess();
        map.put(key, value);
        try {
            pushMapIntoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        checkFileAccess();
        if (exists(key)) {
            map.remove(key);
            keyValueOffset.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        checkFileAccess();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkFileAccess();
        return map.keySet().size() + keyValueOffset.size();
    }

    @Override
    public void close() throws IOException {
        opened = false;
        pushMapIntoFile();
        offsets.writeInt(keyValueOffset.size());
        for (Map.Entry<K, Long> entry : keyValueOffset.entrySet()) {
            keySerialization.write(offsets, entry.getKey());
            offsets.writeLong(entry.getValue());
        }
        storage.close();
        offsets.close();
    }
}
