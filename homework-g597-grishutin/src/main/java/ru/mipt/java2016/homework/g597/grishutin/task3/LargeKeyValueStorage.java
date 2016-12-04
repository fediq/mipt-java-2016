package ru.mipt.java2016.homework.g597.grishutin.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.grishutin.task2.IntegerSerializer;
import ru.mipt.java2016.homework.g597.grishutin.task2.LongSerializer;
import ru.mipt.java2016.homework.g597.grishutin.task2.SerializationStrategy;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class LargeKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {

    private static final double MAX_OBSOLETE_RATIO = 1.0 / 5;
    private static final int MAX_CACHED_ENTRIES = 8;

    private static final String FILENAME_PREFIX = "AzazaDB";
    private static final String VALUES_FILENAME = FILENAME_PREFIX + ".values";
    private static final String OFFSETS_FILENAME = FILENAME_PREFIX + ".valueOffsets";
    private static final String VALUES_SWAP_FILENAME = VALUES_FILENAME + ".tmp";

    private final String path;

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;
    private final SerializationStrategy<Long> longSerializer = LongSerializer.getInstance();
    private final SerializationStrategy<Integer> intSerializer = IntegerSerializer.getInstance();

    private final RandomAccessFile offsetsFile;
    private RandomAccessFile valuesFile;

    private Map<K, Long> valueOffsets = new HashMap<>();

    private final ReadWriteLock lock;
    private boolean isOpen = false;
    private Integer numObsoleteEntries = 0;

    private final LoadingCache<K, V> cached;

    LargeKeyValueStorage(String pathInit,
                         SerializationStrategy<K> keySerializerInit,
                         SerializationStrategy<V> valueSerializerInit) throws IOException {

        path = pathInit;
        keySerializer = keySerializerInit;
        valueSerializer = valueSerializerInit;

        Path valuesPath = Paths.get(path, VALUES_FILENAME);
        Path offsetsPath = Paths.get(path, OFFSETS_FILENAME);

        Files.createDirectories(valuesPath.getParent());

        if (!(Files.exists(valuesPath))) {
            Files.createFile(valuesPath);
        }
        if (!(Files.exists(offsetsPath))) {
            Files.createFile(offsetsPath);
        }

        offsetsFile = new RandomAccessFile(offsetsPath.toFile(), "rw");
        valuesFile = new RandomAccessFile(valuesPath.toFile(), "rw");

        offsetsFile.getChannel().lock();

        lock = new ReentrantReadWriteLock();

        cached = CacheBuilder.newBuilder().maximumSize(MAX_CACHED_ENTRIES).build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws IOException {
                Long offset = valueOffsets.get(key);
                if (offset == null) {
                    return null;
                }

                valuesFile.seek(offset);
                return valueSerializer.deserialize(valuesFile);
            }
        });

        isOpen = true;
        readEntriesFromDisk();
    }

    private void checkOpened() {
        if (!isOpen) {
            throw new IllegalStateException("Storage is closed");
        }
    }

    /*
           file looks like: #obsolete : Integer | (key, offset) | (key, offset) | ...
     */
    private void readEntriesFromDisk() throws IOException {
        if (offsetsFile.length() == 0) {
            return;
        }
        valueOffsets.clear();
        offsetsFile.seek(0);
        cached.cleanUp();
        numObsoleteEntries = intSerializer.deserialize(offsetsFile);
        while (offsetsFile.getFilePointer() < offsetsFile.length()) {
            K key = keySerializer.deserialize(offsetsFile);
            Long offset = offsetsFile.readLong();
            valueOffsets.put(key, offset);
        }
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();
        try {
            checkOpened();
            if (!valueOffsets.containsKey(key)) {
                return null;
            }
            return cached.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException("Guava cache get failed");
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkOpened();
            valuesFile.seek(valuesFile.length());
            valueOffsets.put(key, valuesFile.getFilePointer());
            valueSerializer.serialize(value, valuesFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            checkOpened();
            valueOffsets.remove(key);
            numObsoleteEntries++;
            if (numObsoleteEntries >= size() * MAX_OBSOLETE_RATIO) {
                refreshSSTable();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            checkOpened();
            return valueOffsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkOpened();
            return valueOffsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            checkOpened();
            return valueOffsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (!isOpen) {
            return;
        }

        lock.writeLock().lock();
        isOpen = false;
        try {
            offsetsFile.seek(0);
            offsetsFile.setLength(0);

            intSerializer.serialize(numObsoleteEntries, offsetsFile);

            for (Map.Entry<K, Long> entry : valueOffsets.entrySet()) {
                keySerializer.serialize(entry.getKey(), offsetsFile);
                longSerializer.serialize(entry.getValue(), offsetsFile);
            }

            offsetsFile.close();
            valuesFile.close();
            cached.cleanUp();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void refreshSSTable() {
        lock.readLock().lock();
        lock.writeLock().lock();

        try {
            Map<K, Long> newOffsets = new HashMap<>();

            offsetsFile.seek(0);
            offsetsFile.setLength(0);


            Path newValuesPath = Paths.get(path, VALUES_SWAP_FILENAME);
            Files.createDirectories(newValuesPath.getParent());


            if (!(Files.exists(newValuesPath))) {
                Files.createFile(newValuesPath);
            }

            RandomAccessFile newValuesFile = new RandomAccessFile(newValuesPath.toFile(), "rw");

            intSerializer.serialize(0, offsetsFile);
            for (Map.Entry<K, Long> entry : valueOffsets.entrySet()) {

                Long offset = entry.getValue();
                valuesFile.seek(offset);
                V value = valueSerializer.deserialize(valuesFile);

                newOffsets.put(entry.getKey(), offset);

                keySerializer.serialize(entry.getKey(), offsetsFile);
                longSerializer.serialize(newValuesFile.length(), offsetsFile);
                valueSerializer.serialize(value, newValuesFile);
            }

            newValuesFile.close();
            valuesFile.close();

            Files.move(newValuesPath, Paths.get(path, VALUES_FILENAME), REPLACE_EXISTING);

            // Files.deleteIfExists(Paths.get(path, VALUES_SWAP_FILENAME));

            valuesFile = new RandomAccessFile(Paths.get(path, VALUES_FILENAME).toFile(), "rw");
            valueOffsets = newOffsets;
            numObsoleteEntries = 0;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}