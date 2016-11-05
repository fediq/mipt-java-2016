package ru.mipt.java2016.homework.g595.shakhray.task2.Storage;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.*;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes.IntegerSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

/**
 * Created by Vlad on 26/10/2016.
 */
public class XSStorage<K, V> implements KeyValueStorage<K, V> {

    /**
     * Dealing with concurrency support
     */
    private File lockfile = new File("lock.me");

    /**
     * TRUE is the database is closed.
     * FALSE otherwise.
     */
    private Boolean isStorageClosed = false;

    /**
     * We will need an integer serialization to write
     * the number of entries to file.
     */
    private IntegerSerialization integerSerialization = IntegerSerialization.getSerialization();

    /**
     * HashMap with an actual data.
     */
    private HashMap<K, V> data = new HashMap<K, V>();

    /**
     * An absolute path for the filename with the database.
     */
    private String absoluteStoragePath;
    private final String storageFilename = "storage";

    /**
     * Serializators for keys and values.
     */
    private StorageSerialization<K> keySerialization;
    private StorageSerialization<V> valueSerialization;

    /**
     * Random access file with our database
     */
    private RandomAccessFile file;

    /**
     * Path to the storage
     */
    private String path;

    public XSStorage(String pathPassed, StorageSerialization<K> passedKeySerialization,
                        StorageSerialization<V> passedValueSerialization) throws IOException {
        path = pathPassed;
        if (!lockfile.createNewFile()) {
            throw new IOException("Another process is working.");
        }
        keySerialization = passedKeySerialization;
        valueSerialization = passedValueSerialization;
        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new IOException("Directory not found.");
        }
        absoluteStoragePath = path + File.separator + storageFilename;
        File f = new File(absoluteStoragePath);
        if (!f.createNewFile()) {
            file = new RandomAccessFile(f, "rw");
            loadData();
        } else {
            file = new RandomAccessFile(f, "rw");
        }
    }

    /**
     * Loads data from file
     */
    private void loadData() throws IOException {
        data.clear();
        int size = integerSerialization.read(file);
        for (int i = 0; i < size; i++) {
            data.put(keySerialization.read(file), valueSerialization.read(file));
        }
    }

    /**
     * Writes data to storage file using serializators
     */
    private void save() throws IOException {
        file.seek(0);
        // Clearing the contents of file before writing
        file.setLength(0);
        integerSerialization.write(file, data.size());
        for (K key: data.keySet()) {
            keySerialization.write(file, key);
            valueSerialization.write(file, data.get(key));
        }
        file.close();
    }

    /**
     * Throws an exception is the storage is closed.
     */
    void checkIfStorageIsClosed() {
        if (isStorageClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    @Override
    public V read(K key) {
        checkIfStorageIsClosed();
        return data.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkIfStorageIsClosed();
        return data.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkIfStorageIsClosed();
        data.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkIfStorageIsClosed();
        data.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return data.keySet().iterator();
    }

    @Override
    public int size() {
        checkIfStorageIsClosed();
        return data.size();
    }

    @Override
    public void close() throws IOException {
        checkIfStorageIsClosed();
        lockfile.delete();
        isStorageClosed = true;
        save();
        file.close();
    }
}
