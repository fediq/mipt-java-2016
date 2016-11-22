package ru.mipt.java2016.homework.g597.grishutin.task3;

import ru.mipt.java2016.homework.g597.grishutin.task2.GrishutinKeyValueStorage;
import ru.mipt.java2016.homework.g597.grishutin.task2.SerializationStrategy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LargeKeyValueStorage<K, V> extends GrishutinKeyValueStorage<K, V> {
    private static final Long CACHED = -1L;

    protected HashMap<K, Long> valueOffset = new HashMap<>();
    protected HashSet<K> obsolete;

    protected static final int MAX_KEY_BYTE_SIZE = 50;
    protected static final int MAX_VALUE_BYTE_SIZE = 10_000;
    protected static final int MAX_CACHED_ENTRIES = 15_000_000 /
            (MAX_KEY_BYTE_SIZE + MAX_VALUE_BYTE_SIZE); // 15 Mb RAM for cache

    private static double maxDeletedPart = (double) 4 / 5;
    private static double cacheDropPart = (double) 1 / 10;

    public LargeKeyValueStorage(String directoryPathInit,
                                SerializationStrategy<K> keyStrat,
                                SerializationStrategy<V> valueStrat) throws IOException, IllegalAccessException {
        valueOffset = new HashMap<>();
        obsolete = new HashSet<>();
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
        file looks like:
        (key, value, boolean) | (key, value, boolean) | ...
     */
    @Override
    protected void readEntriesFromDisk() throws IOException {
        Long curOffset = 0L;
        Long fileSize = storageFile.length();
        while (curOffset < fileSize) {
            K key = keySerializationStrategy.deserialize(storageFile);
            V value = valueSerializationStrategy.deserialize(storageFile);
            Long curValueOffset = curOffset + keySerializationStrategy.bytesSize(key);
            if (cached.size() < MAX_CACHED_ENTRIES) {
                cached.put(key, value);
            }

            valueOffset.put(key, curValueOffset);
            numRecords++;
            curOffset = curValueOffset + valueSerializationStrategy.bytesSize(value);
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        checkOpened();

        return cached.containsKey(key) || valueOffset.containsKey(key);
    }

    @Override
    public synchronized V read(K key) {
        if (cached.containsKey(key)) {
            return cached.get(key);
        } else if (valueOffset.containsKey(key)) {
            try {
                storageFile.seek(valueOffset.get(key));
                return valueSerializationStrategy.deserialize(storageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    @Override
    public synchronized void write(K key, V value) {
        if (!exists(key)) {
            numRecords++;
            numEpoch++;
        }
        cached.put(key, value);
        valueOffset.put(key, CACHED);
        if (cached.size() > MAX_CACHED_ENTRIES) {
            try {
                dropCacheOnDisk(cacheDropPart);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void delete(K key) {
        if (!(exists(key))) {
            throw new IllegalArgumentException(String.format("No such key: %s", key.toString()));
        }

        if (cached.containsKey(key)) {
            cached.remove(key);
        }
        if (valueOffset.containsKey(key)) {
            valueOffset.remove(key);
        }
        obsolete.add(key);
        if ((double) obsolete.size() >= numRecords * maxDeletedPart) {
            try {
                refreshSSTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        numRecords--;
        numEpoch++;
    }

    @Override
    public Iterator<K> readKeys() {
        return valueOffset.keySet().iterator();
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            dropCacheOnDisk(1.0);
            refreshSSTable();
            isClosed = true;
        }

        cached.clear();
        valueOffset.clear();

        storageFile.close();
        Files.delete(lock.toPath());
    }

    private void dropCacheOnDisk(double part) throws IOException {
        Long curOffset = storageFile.length();
        storageFile.seek(curOffset);
        Integer dropped = 0;
        HashSet<K> droppedKeys = new HashSet<>();
        for (K key : cached.keySet()) {
            if (dropped < cached.size() * part) {
                keySerializationStrategy.serialize(key, storageFile);
                valueSerializationStrategy.serialize(cached.get(key), storageFile);
                valueOffset.put(key, curOffset + keySerializationStrategy.bytesSize(key));
                droppedKeys.add(key);
                dropped++;
                curOffset += keySerializationStrategy.bytesSize(key) +
                        valueSerializationStrategy.bytesSize(cached.get(key));
            }
        }

        for (K key : droppedKeys) {
            cached.remove(key);
        }
    }

    private void refreshSSTable() throws IOException {
        Path tmpFilePath = Paths.get(directoryPath, storageFilename + ".tmp");
        RandomAccessFile tmpFile = new RandomAccessFile(tmpFilePath.toFile(), "rw");
        for (K key : valueOffset.keySet()) {
            if (valueOffset.get(key).equals(CACHED)) {
                continue;
            }
            keySerializationStrategy.serialize(key, tmpFile);
            storageFile.seek(valueOffset.get(key));
            valueSerializationStrategy.serialize(
                    valueSerializationStrategy.deserialize(storageFile), tmpFile);
        }

        Files.deleteIfExists(Paths.get(directoryPath, storageFilename));
        tmpFile.close();
        File extraTmpFile = tmpFilePath.toFile();
        File databaseFile = Paths.get(directoryPath, storageFilename).toFile();
        if (!extraTmpFile.renameTo(databaseFile)) {
            throw new IOException("Unable to rename file");
        }
        storageFile = new RandomAccessFile(extraTmpFile, "rw");
        obsolete.clear();
    }
}
