package ru.mipt.java2016.homework.g597.grishutin.task3;

import ru.mipt.java2016.homework.g597.grishutin.task2.BooleanSerializationStrategy;
import ru.mipt.java2016.homework.g597.grishutin.task2.GrishutinKeyValueStorage;
import ru.mipt.java2016.homework.g597.grishutin.task2.IntegerSerializationStrategy;
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
    protected HashMap<K, Long> valueOffset = new HashMap<> ();
    protected HashMap<K, Long> tombstoneOffset = new HashMap<> ();
    protected HashSet<K> obsolete = new HashSet<> ();

    protected Integer totalCached;
    protected final int maxKeyByteSize = 50;
    protected final int maxValueByteSize = 10_000;
    protected final int maxCachedEntries = 40_000_000 / (maxKeyByteSize + maxValueByteSize); // 40 Mb RAM for cache
    protected final int BUFFER_SIZE = 1024;

    protected final BooleanSerializationStrategy tombstoneSerializer = BooleanSerializationStrategy.getInstance();
    private double maxDeletedPercentage = 1 / 3;

    public LargeKeyValueStorage(String directoryPath,
                                SerializationStrategy<K> keyStrat,
                                SerializationStrategy<V> valueStrat) throws IOException, IllegalAccessException {
        super(directoryPath, keyStrat, valueStrat);
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
            Boolean valid = tombstoneSerializer.deserialize(storageFile);
            Long curValueOffset = curOffset + keySerializationStrategy.bytesSize(key);
            Long curTombstoneOffset = curValueOffset + valueSerializationStrategy.bytesSize(value);

            if (valid) {
                if (cached.size() < maxCachedEntries) {
                    cached.put(key, value);
                }
                valueOffset.put(key, curValueOffset);
                tombstoneOffset.put(key, curTombstoneOffset);
            }
            curOffset += curTombstoneOffset + tombstoneSerializer.bytesSize(valid);
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        checkOpened();

        return cached.containsKey(key) || valueOffset.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        if (cached.containsKey(key)) {
            numRecords++;
            numEpoch++;
        }
        cached.put(key, value);
        if (cached.size() > maxCachedEntries) {
            try {
                DropCacheOnDisk();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void DropCacheOnDisk() throws IOException {
        storageFile.seek(storageFile.length());
        Integer dropped = 0;
        HashSet<K> droppedKeys = new HashSet<>();
        for (K key : cached.keySet()) {
            if (dropped < cached.size() / 2) {
                keySerializationStrategy.serialize(key, storageFile);
                valueSerializationStrategy.serialize(cached.get(key), storageFile);
                tombstoneSerializer.serialize(true, storageFile);
                droppedKeys.add(key);
                dropped++;
            }
        }

        for (K key : droppedKeys) {
            cached.remove(key);
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
        obsolete.add(key);
        if (((double) obsolete.size()) / numRecords > maxDeletedPercentage) {
            try {
                RefreshSSTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        numRecords--;
        numEpoch++;
    }

    private void RefreshSSTable() throws IOException {
        storageFile.seek(0);
        Long curOffset = 0L;
        Long fileSize = storageFile.length();
        Path tmpFilePath = Paths.get(directoryPath, storageFilename + ".tmp");
        RandomAccessFile tmpFile = new RandomAccessFile(tmpFilePath.toFile(), "w");
        while (curOffset < fileSize) {
            K key = keySerializationStrategy.deserialize(storageFile);
            V value = valueSerializationStrategy.deserialize(storageFile);
            Boolean valid = tombstoneSerializer.deserialize(storageFile);

            if (valid && !obsolete.contains(key)) {
                keySerializationStrategy.serialize(key, tmpFile);
                valueSerializationStrategy.serialize(value, tmpFile);
                tombstoneSerializer.serialize(true, tmpFile);
            }
            curOffset += keySerializationStrategy.bytesSize(key) +
                    valueSerializationStrategy.bytesSize(value) +
                    tombstoneSerializer.bytesSize(true);
        }

        Files.deleteIfExists(Paths.get(directoryPath, storageFilename));
        tmpFile.close();
        File extraTmpFile = tmpFilePath.toFile();
        File databaseFile = Paths.get(directoryPath, storageFilename).toFile();
        if (!extraTmpFile.renameTo(databaseFile)) {
            throw new IOException("Unable to rename file");
        }
        storageFile = new RandomAccessFile(extraTmpFile, "rw");
    }

    private void markDeleted(K key) throws IOException {
        if (tombstoneOffset.containsKey(key)) {
            storageFile.seek(tombstoneOffset.get(key));
            tombstoneSerializer.serialize(false, storageFile);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        return valueOffset.keySet().iterator();
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            DropCacheOnDisk();
            RefreshSSTable();
            isClosed = true;
        }
        cached.clear();
        storageFile.close();
        Files.delete(lock.toPath());
    }
}
