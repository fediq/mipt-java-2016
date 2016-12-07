package ru.mipt.java2016.homework.g597.spirin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by whoami on 11/21/16.
 */
public class HighPerformanceKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private final RandomAccessFile offsetStorage;
    private final RandomAccessFile dataStorage;

    private Map<K, Long> offsets;

    private ReadWriteLock lock;
    private boolean isOpen;

    /**
     *  Suppose that name is a template for name of file storage
     */
    HighPerformanceKeyValueStorage(String path, String name,
                                   SerializationStrategy<K> keySerializer,
                                   SerializationStrategy<V> valueSerializer) throws IOException {

        handleFileExistence(path);

        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        offsetStorage = new RandomAccessFile(path + File.separator + name + ".offset", "rw");
        dataStorage = new RandomAccessFile(path + File.separator + name + ".data", "rw");

        offsetStorage.getChannel().lock();

        offsets = new HashMap<>();

        lock = new ReentrantReadWriteLock();

        loadData();

        isOpen = true;
    }

    private void handleFileExistence(String path) throws FileNotFoundException {
        if (!Files.exists(Paths.get(path))) {
            throw new FileNotFoundException("Passed path is not valid.");
        }
    }

    private void checkIfStorageIsOpen() {
        if (!isOpen) {
            throw new RuntimeException("Storage is not open.");
        }
    }

    private void loadData() throws IOException {
        offsets.clear();
        offsetStorage.seek(0);

        while (offsetStorage.getFilePointer() < offsetStorage.length()) {
            K key = keySerializer.read(offsetStorage);
            long offset = offsetStorage.readLong();
            offsets.put(key, offset);
        }
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();
        try {
            checkIfStorageIsOpen();

            Long offset = offsets.get(key);

            if (offset == null) {
                return null;
            }

            dataStorage.seek(offset);
            return valueSerializer.read(dataStorage);
        } catch (IOException e) {
            throw new RuntimeException("File operation error");
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkIfStorageIsOpen();

            dataStorage.seek(dataStorage.length());
            long offset = dataStorage.getFilePointer();

            valueSerializer.write(dataStorage, value);

            offsets.put(key, offset);
        } catch (IOException e) {
            throw new RuntimeException("File operation error");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            offsets.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            return offsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return offsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            return offsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();
        isOpen = false;
        try {
            offsetStorage.setLength(0);
            offsetStorage.seek(0);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializer.write(offsetStorage, entry.getKey());
                offsetStorage.writeLong(entry.getValue());
            }

            offsetStorage.close();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
