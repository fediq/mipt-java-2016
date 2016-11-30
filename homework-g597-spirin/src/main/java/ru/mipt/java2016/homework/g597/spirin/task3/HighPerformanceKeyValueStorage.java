package ru.mipt.java2016.homework.g597.spirin.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by whoami on 11/21/16.
 */
public class HighPerformanceKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private RandomAccessFile offsetStorage;
    private RandomAccessFile dataStorage;

    private final String path;
    private final String name;

    private Map<K, Long> offsets = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isOpen = false;

    private int countModifyOperations;
    private static final int MAX_MODIFY_OPERATIONS = 5000;

    private LoadingCache<K, V> cache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<K, V>() {
        @Override
        public V load(K key) throws Exception {
            Long offset = offsets.get(key);
            if (offset == null) {
                return null;
            }

            dataStorage.seek(offset);
            return valueSerializer.read(dataStorage);
        }
    });

    /**
     *  Suppose that name is a template for name of file storage
     */
    HighPerformanceKeyValueStorage(String path, String name,
                                   SerializationStrategy<K> keySerializer,
                                   SerializationStrategy<V> valueSerializer) throws IOException {

        this.path = path;
        this.name = name;

        handleFileExistence();

        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        offsetStorage = new RandomAccessFile(path + File.separator + name + ".offset", "rw");
        dataStorage = new RandomAccessFile(path + File.separator + name + ".data", "rw");

        offsetStorage.getChannel().lock();

        loadData();

        isOpen = true;
        countModifyOperations = 0;
    }

    private void handleFileExistence() throws FileNotFoundException {
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
        cache.cleanUp();

        while (offsetStorage.getFilePointer() < offsetStorage.length()) {
            K key = keySerializer.read(offsetStorage);
            long offset = offsetStorage.readLong();
            offsets.put(key, offset);
        }
    }

    private void updateStorage() throws IOException {
        try (RandomAccessFile buffer = new RandomAccessFile(path + File.separator + name + ".buffer", "rw")) {
            Map<K, Long> updatedOffsets = new HashMap<>();

            dataStorage.seek(0);
            while (dataStorage.getFilePointer() < dataStorage.length()) {
                V value = valueSerializer.read(dataStorage);
                K key = keySerializer.read(dataStorage);

                if (offsets.containsKey(key)) {
                    updatedOffsets.put(key, buffer.getFilePointer());
                    valueSerializer.write(buffer, value);
                    keySerializer.write(buffer, key);
                }
            }

            offsets.clear();
            offsets = updatedOffsets;
        }
        dataStorage.close();

        File bufferFile = new File(path + File.separator + name + ".buffer");
        File dataFile = new File(path + File.separator + name + ".data");

        bufferFile.renameTo(dataFile);
        dataStorage = new RandomAccessFile(path + File.separator + name + ".data", "rw");
    }

    @Override
    public V read(K key) {
        lock.writeLock().lock();
        try {
            checkIfStorageIsOpen();
            if (!offsets.containsKey(key)) {
                return null;
            }
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkIfStorageIsOpen();

            if (offsets.containsKey(key)) {
                countModifyOperations += 1;
            }

            dataStorage.seek(dataStorage.length());
            Long offset = dataStorage.getFilePointer();

            offsets.put(key, offset);

            valueSerializer.write(dataStorage, value);
            keySerializer.write(dataStorage, key);

            if (countModifyOperations >= MAX_MODIFY_OPERATIONS) {
                updateStorage();
                countModifyOperations = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            checkIfStorageIsOpen();

            if (offsets.containsKey(key)) {
                countModifyOperations += 1;
            }

            offsets.remove(key);

            if (countModifyOperations >= MAX_MODIFY_OPERATIONS) {
                updateStorage();
                countModifyOperations = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            checkIfStorageIsOpen();
            return offsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkIfStorageIsOpen();
            return offsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            checkIfStorageIsOpen();
            return offsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public void close() throws IOException {
        lock.writeLock().lock();

        if (!isOpen) {
            return;
        }

        isOpen = false;
        try {
            if (countModifyOperations >= MAX_MODIFY_OPERATIONS) {
                updateStorage();
                countModifyOperations = 0;
            }

            offsetStorage.setLength(0);
            offsetStorage.seek(0);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializer.write(offsetStorage, entry.getKey());
                offsetStorage.writeLong(entry.getValue());
            }

            dataStorage.close();
            offsetStorage.close();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
