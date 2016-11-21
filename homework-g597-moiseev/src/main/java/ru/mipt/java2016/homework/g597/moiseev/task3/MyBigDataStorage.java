package ru.mipt.java2016.homework.g597.moiseev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.moiseev.task2.IntegerSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Дисковое хранилище.
 *
 * @author Fedor Moiseev
 * @since 19.11.16
 */

public class MyBigDataStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private static final int MAX_MEM_TABLE_SIZE = 100;
    private static final double MAX_DELETED_PROPORTION = 0.5;
    private static final long IS_NOT_IN_FILE = -1;

    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;
    private final SerializationStrategy<Long> offsetSerializationStrategy = LongSerializationStrategy.getInstance();
    private final SerializationStrategy<Integer> integerSerializationStrategy = 
            IntegerSerializationStrategy.getInstance();
    private String name;
    private String path;
    private RandomAccessFile valuesFile;
    private RandomAccessFile keysFile;
    private File lockFile;
    private HashMap<K, V> memTable;
    private HashMap<K, Long> offsets;
    private int numberOfDeletedElements;
    private boolean isOptimising;
    private boolean isDumping;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    private Lock fileAccessLock = new ReentrantLock();
    private boolean isOpened;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MyBigDataStorage(String path, String name, SerializationStrategy<K> keySerializationStrategy,
                             SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        this.path = path;
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory doesn't exist");
        }

        String lockPath = path + File.separator + name + ".lock";
        lockFile = new File(lockPath);
        if (!lockFile.createNewFile()) {
            throw new IOException("Database is already open");
        }

        memTable = new HashMap<>();
        offsets = new HashMap<>();
        this.name = name;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        String valuesPath = path + File.separator + this.name + "_values.db";
        File values = new File(valuesPath);

        String keysPath = path + File.separator + this.name + "_keys.db";
        File keys = new File(keysPath);

        valuesFile = new RandomAccessFile(values, "rw");
        keysFile = new RandomAccessFile(keys, "rw");

        numberOfDeletedElements = 0;
        isOpened = true;
        isDumping = false;
        isOptimising = false;

        if (!values.createNewFile() || !keys.createNewFile()) {
            loadFromFiles();
        }
    }

    private void loadFromFiles() throws IOException {
        keysFile.seek(0);
        memTable.clear();

        long fileLength = keysFile.length();

        if (fileLength == 0) {
            return;
        }

        numberOfDeletedElements = integerSerializationStrategy.read(keysFile);

        while (keysFile.getFilePointer() < fileLength) {
            K key;
            long offset;
            key = keySerializationStrategy.read(keysFile);
            offset = offsetSerializationStrategy.read(keysFile);
            if (offsets.containsKey(key)) {
                throw new IOException("Duplicate keys");
            } else {
                offsets.put(key, offset);
            }
        }
    }

    private void checkNotClosed() throws IllegalStateException {
        if (!isOpened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    @Override
    public V read(K key) {
        readLock.lock();
        try {
            checkNotClosed();
            Long offset = offsets.get(key);
            if (offset == null) {
                return null;
            }
            if (offset != IS_NOT_IN_FILE) {
                fileAccessLock.lock();
                valuesFile.seek(offset);
                try {
                    return valueSerializationStrategy.read(valuesFile);
                } finally {
                    fileAccessLock.unlock();
                }
            } else {
                return memTable.get(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        writeLock.lock();
        try {
            checkNotClosed();
            return offsets.containsKey(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkNotClosed();
            while (isDumping) {
                Thread.yield();
            }
            if (offsets.containsKey(key) && !memTable.containsKey(key)) {
                numberOfDeletedElements++;
            }

            memTable.put(key, value);
            offsets.put(key, IS_NOT_IN_FILE);

            checkSizeAndDumpOrOptimize();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkNotClosed();
            if (offsets.remove(key) != IS_NOT_IN_FILE) {
                numberOfDeletedElements++;
            }
            memTable.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        readLock.lock();
        checkNotClosed();
        try {
            return offsets.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        writeLock.lock();
        try {
            if (isOpened) {
                isOpened = false;
                dumpMemTable();

                keysFile.seek(0);
                keysFile.setLength(0);

                integerSerializationStrategy.write(keysFile, numberOfDeletedElements);

                for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                    keySerializationStrategy.write(keysFile, entry.getKey());
                    offsetSerializationStrategy.write(keysFile, entry.getValue());
                }

                executorService.shutdown();
                keysFile.close();
                valuesFile.close();
                Files.delete(lockFile.toPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    private void checkSizeAndDumpOrOptimize() {
        if (!isDumping && !isOptimising) {
            if (memTable.size() > MAX_MEM_TABLE_SIZE) {
                dumpMemTable();
            }
        }
    }

    private void dumpMemTable() {
        isDumping = true;
        try {
            long offset = valuesFile.length();
            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                offsets.put(entry.getKey(), offset);
                fileAccessLock.lock();
                try {
                    valuesFile.seek(offset);
                    valueSerializationStrategy.write(valuesFile, entry.getValue());
                    offset = valuesFile.getFilePointer();
                } finally {
                    fileAccessLock.unlock();
                }
            }
            memTable.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isDumping = false;
        }
    }

    private void optimizeMemory() {
        dumpMemTable();
        writeLock.lock();
        isOptimising = true;
        writeLock.unlock();

        try {
            HashMap<K, Long> newOffsets = new HashMap<>();
            String newValuesPath = path + File.separator + "new_" + this.name + "_values.db";
            String valuesPath = path + File.separator + this.name + "_values.db";
            File newValues = new File(newValuesPath);
            File values = new File(valuesPath);

            newValues.createNewFile();

            RandomAccessFile newValuesFile = new RandomAccessFile(newValues, "rw");
            newValuesFile.seek(0);
            newValuesFile.setLength(0);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                fileAccessLock.lock();
                try {
                    valuesFile.seek(entry.getValue());
                    V value = valueSerializationStrategy.read(valuesFile);
                    newOffsets.put(entry.getKey(), newValuesFile.length());
                    valueSerializationStrategy.write(newValuesFile, value);
                } finally {
                    readLock.unlock();
                }
            }

            readLock.lock();
            numberOfDeletedElements = 0;
            offsets = newOffsets;
            valuesFile = newValuesFile;
            Files.delete(values.toPath());
            if (!newValues.renameTo(values)) {
                throw new IOException("Can't rename values file");
            }
            readLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isOptimising = false;
        }
    }
}
