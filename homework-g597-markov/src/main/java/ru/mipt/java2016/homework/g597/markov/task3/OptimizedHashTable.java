package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 24.11.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class OptimizedHashTable<K, V> implements KeyValueStorage<K, V> {
    private final String databaseName = "storage.db";
    private final String databasePath;
    private final String keysFileName = "keys_" + databaseName;
    private final String valuesFileName = "values_" + databaseName;

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;
    private final Map<K, Long> offsets = new HashMap<>();
    private RandomAccessFile keysFile;
    private RandomAccessFile valuesFile;
    private boolean closed = false;

    public OptimizedHashTable(String path,
                              SerializationStrategy<K> serializerKeys,
                              SerializationStrategy<V> serializerValues)
            throws IOException {

        keySerializer = serializerKeys;
        valueSerializer = serializerValues;
        databasePath = path + File.separator;
        File databaseFile;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            databaseFile = new File(databasePath + databaseName);
        } else {
            throw new IllegalArgumentException("path" + path + " is not available");
        }
        if (databaseFile.createNewFile()) {
            createStorage();
        } else {
            openStorage();
        }
    }

    private synchronized void createStorage() throws IOException {
        File fileK = new File(databasePath + keysFileName);
        File fileV = new File(databasePath + valuesFileName);

        keysFile = new RandomAccessFile(fileK, "rw");
        valuesFile = new RandomAccessFile(fileV, "rw");
    }

    private void openStorage() throws IOException {
        File fileK = new File(databasePath + keysFileName);
        File fileV = new File(databasePath + valuesFileName);

        if (!fileK.exists() || !fileV.exists()) {
            throw new IOException("database does not exist");
        }

        keysFile = new RandomAccessFile(fileK, "rw");
        valuesFile = new RandomAccessFile(fileV, "rw");

        getKeysAndOffsets();
    }

    private void getKeysAndOffsets() throws IOException {
        int numberOfKeys = (new IntegerSerializator()).read(keysFile);
        for (int i = 0; i < numberOfKeys; i++) {
            K key = keySerializer.read(keysFile);
            Long offset = (new LongSerializator()).read(keysFile);
            offsets.put(key, offset);
        }
    }

    @Override
    public synchronized V read(K key) {
        checkForClosed();
        if (!offsets.containsKey(key)) {
            return null;
        }

        Long offset = offsets.get(key);
        try {
            valuesFile.seek(offset);
            return valueSerializer.read(valuesFile);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        checkForClosed();
        return offsets.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        checkForClosed();
        try {
            offsets.put(key, valuesFile.length());
            valuesFile.seek(valuesFile.length());
            valueSerializer.write(valuesFile, value);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public synchronized void delete(K key) {
        checkForClosed();
        if (offsets.containsKey(key)) {
            offsets.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkForClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        checkForClosed();
        return offsets.size();
    }

    private void checkForClosed() {
        if (closed) {
            throw new IllegalStateException("database is closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            keysFile.seek(0);
            (new IntegerSerializator()).write(keysFile, offsets.size());
            SerializationStrategy<Long> longSerializator = new LongSerializator();
            for (K key : offsets.keySet()) {
                keySerializer.write(keysFile, key);
                longSerializator.write(keysFile, offsets.get(key));
            }
            offsets.clear();

            keysFile.close();
            valuesFile.close();
            closed = true;
        }
    }
}

