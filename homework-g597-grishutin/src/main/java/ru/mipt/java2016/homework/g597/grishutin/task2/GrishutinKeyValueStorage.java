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
    private File lock;
    private HashMap<K, V> kvHashMap = new HashMap<>();

    private Integer numEpoch = 0;
    private int numRecords = 0;
    private boolean isClosed = false;

    GrishutinKeyValueStorage(String directoryPath,
                             SerializationStrategy<K> keyStrat,
                             SerializationStrategy<V> valueStrat) throws IOException, IllegalAccessException {

        final String preferredFilename = "Azazaza.db";
        keySerializationStrategy = keyStrat;
        valueSerializationStrategy = valueStrat;
        Path filePath = Paths.get(directoryPath, preferredFilename);
        Path lockFilePath = Paths.get(directoryPath, preferredFilename + ".lock");

        lock = lockFilePath.toFile();
        if (!lock.createNewFile()) { // if lock is hold by other kvStorage
            throw new IllegalAccessException("Database is already opened");
        }

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
        checkOpened();

        return kvHashMap.get(key);
    }

    private void checkOpened() {
        if (isClosed) {
            throw new IllegalStateException("Storage is closed!");
        }
    }

    @Override
    public boolean exists(K key) {
        checkOpened();
        return kvHashMap.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        checkOpened();
        if (!(exists(key))) {
            numRecords++;
        }
        kvHashMap.put(key, value);
        numEpoch++;
    }

    @Override
    public synchronized void delete(K key) {
        checkOpened();
        if (!(exists(key))) {
            throw new IllegalArgumentException(String.format("No such key: %s", key.toString()));
        }
        kvHashMap.remove(key);
        numEpoch++;
        numRecords--;
    }

    @Override
    public Iterator<K> readKeys() {
        checkOpened();
        return kvHashMap.keySet().iterator();
    }

    @Override
    public int size() {
        checkOpened();
        return numRecords;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            overwrite();
            isClosed = true;
        }
        kvHashMap.clear();
        storageFile.close();
        Files.delete(lock.toPath());
    }

    private void overwrite() throws IOException {
        storageFile.seek(0);
        storageFile.setLength(0);
        IntegerSerializationStrategy integerSerializationStrategy = IntegerSerializationStrategy.getInstance();
        integerSerializationStrategy.serialize(numRecords, storageFile);
        for (Map.Entry<K, V> entry: kvHashMap.entrySet()) {
            keySerializationStrategy.serialize(entry.getKey(), storageFile);
            valueSerializationStrategy.serialize(entry.getValue(), storageFile);
        }
    }
}