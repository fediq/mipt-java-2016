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


public class MyOptimisedKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private static final int MAX_MEMTABLE_SIZE = 1000;
    private static final double MAX_DELETED_RATIO = 0.1;

    private int numberOfDeletedElements;
    private HashMap<K, V> memtable;
    private HashMap<K, Long> offsets;
    private final SerializationStrategy<K, V> serializationStrategy;
    private final RandomAccessFile fileForKeys;
    private RandomAccessFile fileForValues;
    private final String fileName;
    private final String path;
    private final File lock;
    private boolean isDumpingInProcess;

    public MyOptimisedKeyValueStorage(String path,
                                      SerializationStrategy<K, V> serializationStrategy) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory");
        }
        this.path = path;
        this.fileName = "MyOptimisedKeyValueStorage";
        lock = new File(path + File.separator + fileName + ".lock");
        if (!lock.createNewFile()) {
            throw new IOException("Database is already open");
        }

        memtable = new HashMap<>();
        offsets = new HashMap<>();
        this.serializationStrategy = serializationStrategy;

        File tmpFileForValues = new File(path + File.separator + this.fileName + ".values");
        fileForValues = new RandomAccessFile(tmpFileForValues, "rw");

        File tmpFileForKeys = new File(path + File.separator + this.fileName + ".keys");
        fileForKeys = new RandomAccessFile(tmpFileForKeys, "rw");

        numberOfDeletedElements = 0;
        isDumpingInProcess = false;

        if (!tmpFileForValues.createNewFile() || !tmpFileForKeys.createNewFile()) {
            initializeFromFile();
        }
    }

    private void initializeFromFile() throws IOException {
        fileForKeys.seek(0);
        long fileLen = fileForKeys.length();

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
        if (fileForKeys == null || fileForValues == null) {
            throw new IllegalStateException("Something went wrong");
        }
        try {
            if (memtable.containsKey(key)) {
                return memtable.get(key);
            } else if (offsets.containsKey(key)) {
                fileForValues.seek(offsets.get(key));
                return serializationStrategy.readValue(fileForValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void confirmNotClosed() {
        if (fileForKeys == null || fileForValues == null) {
            throw new IllegalStateException("Storage was closed");
        }
    }

    @Override
    public boolean exists(K key) {
        confirmNotClosed();
        return memtable.containsKey(key) || offsets.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        confirmNotClosed();
        if (isDumpingInProcess) {
            Thread.yield();
        }
        if (offsets.containsKey(key) && !memtable.containsKey(key)) {
            ++numberOfDeletedElements;
        }
        memtable.put(key, value);
        offsets.put(key, (long) -1);
        dumpOrClearDeletedElementIfNecessary();
    }

    @Override
    public void delete(K key) {
        confirmNotClosed();
        if (offsets.containsKey(key)) {
            numberOfDeletedElements++;
            offsets.remove(key);
        }
        memtable.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        confirmNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        confirmNotClosed();
        return offsets.size();
    }

    @Override
    public void close() throws IOException {
        try {
            dumpMemtable();
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
        }
    }

    private void dumpMemtable() {
        isDumpingInProcess = true;
        try {
            long offset = fileForValues.length();
            for (Map.Entry<K, V> entry : memtable.entrySet()) {
                offsets.put(entry.getKey(), offset);
                fileForValues.seek(offset);
                serializationStrategy.writeValue(fileForValues, entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isDumpingInProcess = false;
        }
    }

    private void clearDeletedElements() {
        try {
            HashMap<K, Long> updatedOffsets = new HashMap<>();
            File tmpFileForValues = new File(path + File.separator + this.fileName + ".values");
            File tmpNewFileForValues = new File(path + File.separator + this.fileName + ".newValues");

            if (!tmpNewFileForValues.createNewFile()) {
                throw new IllegalStateException("Unable to create necessary file");
            }
            RandomAccessFile newFileForValues = new RandomAccessFile(tmpNewFileForValues, "rw");
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                fileForValues.seek(entry.getValue());
                V value = serializationStrategy.readValue(fileForValues);
                updatedOffsets.put(entry.getKey(), newFileForValues.length());
                serializationStrategy.writeValue(newFileForValues, value);
            }
            numberOfDeletedElements = 0;
            offsets = updatedOffsets;
            fileForValues = newFileForValues;
            Files.delete(tmpFileForValues.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dumpOrClearDeletedElementIfNecessary() {
        if (numberOfDeletedElements > MAX_DELETED_RATIO * size()) {
            clearDeletedElements();
        }
        if (memtable.size() > MAX_MEMTABLE_SIZE) {
            dumpMemtable();
        }
    }
}
