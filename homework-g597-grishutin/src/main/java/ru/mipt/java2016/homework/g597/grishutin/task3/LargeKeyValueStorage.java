package ru.mipt.java2016.homework.g597.grishutin.task3;

import ru.mipt.java2016.homework.g597.grishutin.task2.GrishutinKeyValueStorage;
import ru.mipt.java2016.homework.g597.grishutin.task2.LongSerializationStrategy;
import ru.mipt.java2016.homework.g597.grishutin.task2.SerializationStrategy;
import ru.mipt.java2016.homework.g597.grishutin.task3.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LargeKeyValueStorage<K, V> extends GrishutinKeyValueStorage<K, V> {
    private static final Long CACHED = -1L;

    protected HashMap<K, Long> valueOffset = new HashMap<>();

    protected static final int MAX_KEY_BYTE_SIZE = 50;
    protected static final int MAX_VALUE_BYTE_SIZE = 10_000;
    /*protected static final int MAX_CACHED_ENTRIES = 1_000_000 /
            (MAX_KEY_BYTE_SIZE + MAX_VALUE_BYTE_SIZE); // 1 Mb RAM for cache
    */
    protected static final int MAX_CACHED_ENTRIES = 100;

    private static final double MAX_DELETED_RATIO = (double) 1 / 2;
    private static final double DEFAULT_CACHE_DROP_RATIO = (double) 1;

    protected RandomAccessFile offsetsFile;
    protected RandomAccessFile valuesFile;

    protected LongSerializationStrategy longSerializationStrategy = LongSerializationStrategy.getInstance();

    protected Integer obsoleteSize;


    protected final String lockFilename = storageFilename + ".lock";
    protected final String valuesFilename = storageFilename + ".values";
    protected final String offsetsFilename = storageFilename + ".offsets";
    protected final String valuesSwapFilename = storageFilename + ".values.tmp";

    public LargeKeyValueStorage(String directoryPathInit,
                                SerializationStrategy<K> keyStrat,
                                SerializationStrategy<V> valueStrat) throws IOException, IllegalAccessException {
        valueOffset = new HashMap<>();
        obsoleteSize = 0;
        directoryPath = directoryPathInit;
        keySerializationStrategy = keyStrat;
        valueSerializationStrategy = valueStrat;
        Path valuesFilePath = Paths.get(directoryPath, valuesFilename);
        Path offsetsFilePath = Paths.get(directoryPath, offsetsFilename);
        Path lockFilePath = Paths.get(directoryPath, lockFilename);

        Files.createDirectories(valuesFilePath.getParent());

        lock = lockFilePath.toFile();
        if (!lock.createNewFile()) { // if lock is hold by other kvStorage
            throw new IllegalAccessException("Database is already opened");
        }


        if (!(Files.exists(valuesFilePath))) {
            Files.createFile(valuesFilePath);
        }
        if (!(Files.exists(offsetsFilePath))) {
            Files.createFile(offsetsFilePath);
        }

        valuesFile = new RandomAccessFile(valuesFilePath.toFile(), "rw");
        offsetsFile = new RandomAccessFile(offsetsFilePath.toFile(), "rw");

        if (valuesFile.length() != 0) {
            readEntriesFromDisk();
        }
    }

    /*
        files look like:
        offsetsFile: (key, offset) | (key, offset) | ...
                             __|
                            |
        valuesFile: (value) | (value) | ...
     */
    @Override
    protected void readEntriesFromDisk() throws IOException {
        offsetsFile.seek(0);
        Long fileSize = offsetsFile.length();
        Integer curCached = 0;
        List<Pair<K, Long>> offsets = new ArrayList<>();
        while (offsetsFile.getFilePointer() < fileSize) {
            K key = keySerializationStrategy.deserialize(offsetsFile);
            Long offset = longSerializationStrategy.deserialize(offsetsFile);
            offsets.add(new Pair<>(key, offset));
        }
        Collections.sort(offsets, (lhs, rhs) -> {
            //     return 1 if rhs should be before lhs
            //     return -1 if lhs should be before rhs
            //     return 0 otherwise
            if (lhs.getSecond() < rhs.getSecond()) {
                return -1;
            } else if (lhs.getSecond() > rhs.getSecond()) {
                return 1;
            } else {
                return 0;
            }
        });

        for (Pair<K, Long> p: offsets) {
            K key = p.getFirst();
            Long offset = p.getSecond();
            valuesFile.seek(offset);
            V value = valueSerializationStrategy.deserialize(valuesFile);
            if (curCached < MAX_CACHED_ENTRIES) {
                cached.put(key, value);
                valueOffset.put(key, CACHED);
                curCached++;
            } else {
                valueOffset.put(key, offset);
            }
            numRecords++;
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
                valuesFile.seek(valueOffset.get(key));
                return valueSerializationStrategy.deserialize(valuesFile);
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
                dropCacheOnDisk(DEFAULT_CACHE_DROP_RATIO);
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
        } else {
            obsoleteSize++;
        }
        if (valueOffset.containsKey(key)) {
            valueOffset.remove(key);
        }
        if ((double) obsoleteSize >= numRecords * MAX_DELETED_RATIO) {
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

        offsetsFile.seek(0);
        offsetsFile.setLength(0);
        for (Map.Entry<K, Long> entry: valueOffset.entrySet()) {
            keySerializationStrategy.serialize(entry.getKey(), offsetsFile);
            longSerializationStrategy.serialize(entry.getValue(), offsetsFile);
        }

        cached.clear();
        valueOffset.clear();
        offsetsFile.seek(0);
        valuesFile.seek(0);

        offsetsFile.close();
        valuesFile.close();
        Files.delete(lock.toPath());
    }

    private void dropCacheOnDisk(double part) throws IOException {
        valuesFile.seek(valuesFile.getFilePointer());

        Integer dropped = 0;
        HashSet<K> droppedKeys = new HashSet<>();

        for (K key : cached.keySet()) {
            if (dropped < cached.size() * part) {
                valueOffset.put(key, valuesFile.getFilePointer());
                valueSerializationStrategy.serialize(cached.get(key), valuesFile);
                droppedKeys.add(key);
                dropped++;
            }
        }

        for (K key : droppedKeys) {
            cached.remove(key);
        }
    }

    private synchronized void refreshSSTable() throws IOException {
        Path tmpValuesFilePath = Paths.get(directoryPath, valuesSwapFilename);

        RandomAccessFile tmpValuesFile = new RandomAccessFile(tmpValuesFilePath.toFile(), "rw");

        HashMap<K, Long> newValueOffsets = new HashMap<>();
        for (K key : valueOffset.keySet()) {
            if (valueOffset.get(key).equals(CACHED)) {
                continue;
            }
            Long offset = valueOffset.get(key);
            valuesFile.seek(offset);
            V value = valueSerializationStrategy.deserialize(valuesFile);

            newValueOffsets.put(key, tmpValuesFile.getFilePointer());
            valueSerializationStrategy.serialize(value, tmpValuesFile);

        }

        Files.deleteIfExists(Paths.get(directoryPath, valuesFilename));
        tmpValuesFile.close();

        File extraTmpValuesFile = tmpValuesFilePath.toFile();

        File valuesFileRenamer = Paths.get(directoryPath, valuesFilename).toFile();
        if (!extraTmpValuesFile.renameTo(valuesFileRenamer)) {
            throw new IOException("Unable to rename file");
        }

        valuesFile = new RandomAccessFile(extraTmpValuesFile, "rw");
        valueOffset = newValueOffsets;
        obsoleteSize = 0;
    }
}


