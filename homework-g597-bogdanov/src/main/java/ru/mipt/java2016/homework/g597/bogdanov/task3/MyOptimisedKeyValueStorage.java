package ru.mipt.java2016.homework.g597.bogdanov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.bogdanov.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MyOptimisedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int MAX_MEMTABLE_SIZE = 1000;
    private static final double MAX_DELETED_RATIO = 0.1;
    private static final int MIN_SIZE_TO_CLEAR = 100;

    private int numberOfDeletedElements;
    private final Map<K, V> memtable = new HashMap<>();
    private Map<K, Long> offsets = new HashMap<>();
    private final SerializationStrategy<K, V> serializationStrategy;
    private final RandomAccessFile fileForKeys;
    private RandomAccessFile fileForValues;
    private final String fileName;
    private final String path;
    private final File lock;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ReentrantLock fileLock = new ReentrantLock();

    public MyOptimisedKeyValueStorage(String path,
                                      SerializationStrategy<K, V> serializationStrategy) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory");
        }
        this.path = path;
        this.fileName = "MyOptimisedKeyValueStorage";
        lock = new File(path + File.separator + fileName + "_lock");
        if (!lock.createNewFile()) {
            throw new IOException("Database is already open");
        }

        this.serializationStrategy = serializationStrategy;

        File tmpFileForValues = new File(path + File.separator + this.fileName + "_values");
        fileForValues = new RandomAccessFile(tmpFileForValues, "rw");

        File tmpFileForKeys = new File(path + File.separator + this.fileName + "_keys");
        fileForKeys = new RandomAccessFile(tmpFileForKeys, "rw");

        numberOfDeletedElements = 0;

        tmpFileForValues.createNewFile();
        tmpFileForKeys.createNewFile();
        initializeFromFile();
    }

    private void initializeFromFile() throws IOException {
        fileForKeys.seek(0);
        long fileLen = fileForKeys.length();

        if (fileLen == 0) {
            return;
        }

        numberOfDeletedElements = fileForKeys.readInt();

        while (fileForKeys.getFilePointer() < fileLen) {
            K key = serializationStrategy.readKey(fileForKeys);
            long offset = fileForKeys.readLong();
            if (offsets.containsKey(key)) {
                throw new IOException("Duplicates are not allowed");
            } else {
                offsets.put(key, offset);
            }
        }
    }

    @Override
    public V read(K key) {
        readWriteLock.readLock().lock();
        if (fileForKeys == null || fileForValues == null) {
            throw new IllegalStateException("Something went wrong");
        }
        try {
            if (memtable.containsKey(key)) {
                return memtable.get(key);
            } else if (offsets.containsKey(key)) {
                fileForValues.seek(offsets.get(key));
                fileLock.lock();
                try {
                    return serializationStrategy.readValue(fileForValues);
                } finally {
                    fileLock.unlock();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e + "something went wrong");
        } finally {
            readWriteLock.readLock().unlock();
        }
        return null;
    }

    private void confirmNotClosed() {
        if (fileForValues == null) {
            throw new IllegalStateException("Storage was closed");
        }
    }

    @Override
    public boolean exists(K key) {
        confirmNotClosed();
        readWriteLock.readLock().lock();
        try {
            return memtable.containsKey(key) || offsets.containsKey(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        readWriteLock.writeLock().lock();
        try {
            confirmNotClosed();
            if (offsets.containsKey(key) && !memtable.containsKey(key)) {
                ++numberOfDeletedElements;
            }
            memtable.put(key, value);
            offsets.put(key, (long) -1);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        dumpOrClearDeletedElementsIfNecessary();
    }

    @Override
    public void delete(K key) {
        readWriteLock.writeLock().lock();
        try {
            confirmNotClosed();
            if (offsets.containsKey(key)) {
                numberOfDeletedElements++;
                offsets.remove(key);
            }
            memtable.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        confirmNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        readWriteLock.writeLock().lock();
        confirmNotClosed();
        try {
            return offsets.size();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (fileForValues != null) {
            try {
                dumpMemtable();
                readWriteLock.writeLock().lock();
                fileForKeys.seek(0);
                fileForKeys.setLength(0);
                fileForKeys.writeInt(numberOfDeletedElements);
                for (HashMap.Entry<K, Long> entry : offsets.entrySet()) {
                    serializationStrategy.writeKey(fileForKeys, entry.getKey());
                    fileForKeys.writeLong(entry.getValue());
                }
            } finally {
                Files.delete(lock.toPath());
                memtable.clear();
                offsets.clear();
                fileForKeys.close();
                fileForValues.close();
                fileForValues = null;
                readWriteLock.writeLock().unlock();
            }
        }
    }

    private void dumpMemtable() {
        readWriteLock.writeLock().lock();
        try {
            long offset = fileForValues.length();
            for (Map.Entry<K, V> entry : memtable.entrySet()) {
                offsets.put(entry.getKey(), offset);
                fileLock.lock();
                try {
                    fileForValues.seek(offset);
                    serializationStrategy.writeValue(fileForValues, entry.getValue());
                    offset = fileForValues.getFilePointer();
                } finally {
                    fileLock.unlock();
                }
            }
            memtable.clear();
        } catch (IOException e) {
            throw new IllegalStateException(e + "something went wrong");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void clearDeletedElements() {
        dumpMemtable();
        readWriteLock.writeLock().lock();
        try {
            HashMap<K, Long> updatedOffsets = new HashMap<>();
            File tmpFileForValues = new File(path + File.separator + this.fileName + "_values");
            File tmpNewFileForValues = new File(path + File.separator + this.fileName + "_newValues");

            if (!tmpNewFileForValues.createNewFile()) {
                throw new IllegalStateException("Unable to create necessary file");
            }
            RandomAccessFile newFileForValues = new RandomAccessFile(tmpNewFileForValues, "rw");
            fileLock.lock();
            try {
                for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                    fileLock.lock();
                    fileForValues.seek(entry.getValue());
                    V value = serializationStrategy.readValue(fileForValues);
                    updatedOffsets.put(entry.getKey(), newFileForValues.length());
                    serializationStrategy.writeValue(newFileForValues, value);
                    fileLock.unlock();
                }
            } finally {
                fileLock.unlock();
            }
            numberOfDeletedElements = 0;
            offsets = updatedOffsets;
            fileForValues = newFileForValues;
            Files.delete(tmpFileForValues.toPath());
        } catch (IOException e) {
            throw new IllegalStateException(e + "something went wrong");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void dumpOrClearDeletedElementsIfNecessary() {
        if (numberOfDeletedElements > MAX_DELETED_RATIO * size() && size() > MIN_SIZE_TO_CLEAR) {
            clearDeletedElements();
        }
        if (memtable.size() > MAX_MEMTABLE_SIZE) {
            dumpMemtable();
        }
    }
}
