package ru.mipt.java2016.homework.g597.grishutin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class GrishutinKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    protected final SerializationStrategy<K> keySerializationStrategy;
    protected final SerializationStrategy<V> valueSerializationStrategy;
    protected RandomAccessFile storageFile;
    protected final File lock;

    protected final HashMap<K, V> cached = new HashMap<>();

    protected Integer numEpoch = 0;
    protected int numRecords = 0;
    protected boolean isClosed = false;

    protected final String storageFilename = "Azazaza.db";
    protected final String directoryPath;

    public GrishutinKeyValueStorage(String directoryPathInit,
                             SerializationStrategy<K> keyStrat,
                             SerializationStrategy<V> valueStrat) throws IOException, IllegalAccessException {
        directoryPath = directoryPathInit;
        keySerializationStrategy = keyStrat;
        valueSerializationStrategy = valueStrat;
        Path filePath = Paths.get(directoryPath, storageFilename);
        Path lockFilePath = Paths.get(directoryPath, storageFilename + ".lock");

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
            readEntriesFromDisk();
        }
    }

    /*
        File with SSTable looks like:
        numEntries: int | (key, value, tombstone : boolean) | (key, value, tombstone : boolean) | ....
        tombstone
     */
    protected void readEntriesFromDisk() throws IOException {
        numRecords = storageFile.readInt();
        for (int i = 0; i < numRecords; ++i) {
            K key = keySerializationStrategy.deserialize(storageFile);
            V value = valueSerializationStrategy.deserialize(storageFile);

            cached.put(key, value);
        }
    }

    @Override
    public synchronized V read(K key) {
        checkOpened();

        return cached.get(key);
    }

    @Override
    public synchronized boolean exists(K key) {
        checkOpened();
        return cached.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        checkOpened();
        if (!(exists(key))) {
            numRecords++;
        }
        cached.put(key, value);
        numEpoch++;
    }

    @Override
    public synchronized void delete(K key) {
        checkOpened();
        if (!(exists(key))) {
            throw new IllegalArgumentException(String.format("No such key: %s", key.toString()));
        }
        cached.remove(key);
        numEpoch++;
        numRecords--;
    }

    @Override
    public Iterator<K> readKeys() {
        checkOpened();
        return cached.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        checkOpened();
        return numRecords;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            overwrite();
            isClosed = true;
        }
        cached.clear();
        storageFile.close();
        Files.delete(lock.toPath());
    }

    protected void checkOpened() {
        if (isClosed) {
            throw new IllegalStateException("Storage is closed!");
        }
    }

    protected void overwrite() throws IOException {
        storageFile.seek(0);
        storageFile.setLength(0);
        IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
        integerSerializer.serialize(numRecords, storageFile);
        for (Map.Entry<K, V> entry: cached.entrySet()) {
            keySerializationStrategy.serialize(entry.getKey(), storageFile);
            valueSerializationStrategy.serialize(entry.getValue(), storageFile);
        }
    }
}