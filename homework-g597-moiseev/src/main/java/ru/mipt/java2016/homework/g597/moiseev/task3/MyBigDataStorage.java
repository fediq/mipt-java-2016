package ru.mipt.java2016.homework.g597.moiseev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.moiseev.task2.IntegerSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    private final String name;
    private final String path;
    private RandomAccessFile valuesFile;
    private RandomAccessFile keysFile;
    private Map<K, V> memTable = new HashMap<>();
    private Map<K, Long> offsets = new HashMap<>();
    private int numberOfDeletedElements = 0;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    private Lock fileAccessLock = new ReentrantLock();
    private boolean isOpened = true;

    public MyBigDataStorage(String path, String name, SerializationStrategy<K> keySerializationStrategy,
                            SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        this.path = path;
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory doesn't exist");
        }

        this.name = name;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        String valuesPath = path + File.separator + this.name + "_values.db";
        File values = new File(valuesPath);

        String keysPath = path + File.separator + this.name + "_keys.db";
        File keys = new File(keysPath);

        valuesFile = new RandomAccessFile(values, "rw");
        keysFile = new RandomAccessFile(keys, "rw");

        if (!values.createNewFile() || !keys.createNewFile()) {
            loadFromFiles();
        }

        try {
            keysFile.getChannel().tryLock();
        } catch (IOException e) {
            throw new IOException("Database is already open");
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
                try {
                    valuesFile.seek(offset);
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

            memTable.put(key, value);
            Long previousOffset = offsets.put(key, IS_NOT_IN_FILE);

            if (previousOffset != null && previousOffset != IS_NOT_IN_FILE) {
                numberOfDeletedElements++;
            }

            checkSizeAndDumpOrOptimize();
        } catch (IOException e) {
            e.printStackTrace();
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

                keysFile.close();
                valuesFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    private void checkSizeAndDumpOrOptimize() throws IOException {
        if (memTable.size() > MAX_MEM_TABLE_SIZE) {
            dumpMemTable();
        }
        if (numberOfDeletedElements / offsets.size() > MAX_DELETED_PROPORTION) {
            optimizeMemory();
        }

    }

    private void dumpMemTable() throws IOException {
        long offset = valuesFile.length();
        valuesFile.seek(offset);
        for (Map.Entry<K, V> entry : memTable.entrySet()) {
            offsets.put(entry.getKey(), offset);
            valueSerializationStrategy.write(valuesFile, entry.getValue());
            offset = valuesFile.getFilePointer();
        }
        memTable.clear();
    }

    private void optimizeMemory() {
        try {
            Map<K, Long> newOffsets = new HashMap<>();
            String newValuesPath = path + File.separator + "new_" + this.name + "_values.db";
            String valuesPath = path + File.separator + this.name + "_values.db";
            File newValues = new File(newValuesPath);
            File values = new File(valuesPath);

            newValues.createNewFile();

            RandomAccessFile newValuesFile = new RandomAccessFile(newValues, "rw");
            newValuesFile.seek(0);
            newValuesFile.setLength(0);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                if (entry.getValue() == IS_NOT_IN_FILE) {
                    newOffsets.put(entry.getKey(), IS_NOT_IN_FILE);
                } else {
                    valuesFile.seek(entry.getValue());
                    V value = valueSerializationStrategy.read(valuesFile);
                    newOffsets.put(entry.getKey(), newValuesFile.length());
                    valueSerializationStrategy.write(newValuesFile, value);
                }
            }

            numberOfDeletedElements = 0;
            offsets = newOffsets;
            valuesFile.close();
            valuesFile = newValuesFile;
            System.gc();
            while (true) {
                try {
                    Files.delete(values.toPath());
                    break;
                } catch (FileSystemException e) {
                    e.printStackTrace();
                }
            }
            if (!newValues.renameTo(values)) {
                throw new IOException("Can't rename values file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}