package ru.mipt.java2016.homework.g597.grishutin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


class GrishutinKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;
    private RandomAccessFile storageFile;
    private HashMap<K, V> kvHashMap;

    private Integer numEpoch;
    private int numRecords;
    private boolean isClosed = false;

    GrishutinKeyValueStorage(String directoryPath,
                             SerializationStrategy<K> keyStrat,
                             SerializationStrategy<V> valueStrat) throws IOException {

        String preferredFilename = "Azazaza.db";
        keySerializationStrategy = keyStrat;
        valueSerializationStrategy = valueStrat;
        kvHashMap = new HashMap<>();
        numEpoch = 0;
        numRecords = 0;
        Path filePath = Paths.get(directoryPath + File.separator + preferredFilename);
        Files.createDirectories(filePath.getParent());

        if (!(Files.exists(filePath))) {
            Files.createFile(filePath);
        }

        storageFile = new RandomAccessFile(filePath.toFile(), "rw");
        if (storageFile.length() != 0) {
            readData();
        }
    }

    private void readData() throws IOException {
        numRecords = storageFile.readInt();
        for (int i = 0; i < numRecords; ++i) {
            K key = keySerializationStrategy.deserialize(storageFile);
            V value = valueSerializationStrategy.deserialize(storageFile);
            kvHashMap.put(key, value);
        }
    }

    @Override
    public V read(K key) {
        return kvHashMap.get(key);
    }

    @Override
    public boolean exists(K key) {
        return kvHashMap.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        if (!(exists(key))) {
            numRecords++;
        }
        kvHashMap.put(key, value);
        numEpoch++;
    }

    @Override
    public synchronized void delete(K key) {
        if (!(exists(key))) {
            throw new IllegalArgumentException(String.format("No such key: %s", key.toString()));
        }
        kvHashMap.remove(key);
        numEpoch++;
        numRecords--;
    }

    @Override
    public Iterator<K> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return numRecords;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            overwrite();
            isClosed = true;
        }
    }

    private void overwrite() throws IOException {
        IntegerSerializationStrategy integerSerializationStrategy = IntegerSerializationStrategy.INSTANCE;
        integerSerializationStrategy.serialize(numRecords, storageFile);
        for (Map.Entry<K, V> entry: kvHashMap.entrySet()) {
            keySerializationStrategy.serialize(entry.getKey(), storageFile);
            valueSerializationStrategy.serialize(entry.getValue(), storageFile);
        }
    }
}