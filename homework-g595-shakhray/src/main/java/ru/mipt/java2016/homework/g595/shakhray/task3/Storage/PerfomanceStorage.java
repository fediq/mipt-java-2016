package ru.mipt.java2016.homework.g595.shakhray.task3.Storage;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.IntegerSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.LongSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Interface.StorageSerialization;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Vlad on 26/10/2016.
 */
public class PerfomanceStorage<K, V> implements KeyValueStorage<K, V> {

    /**
     * Dealing with concurrency support
     */
//    private File lockfile = new File("lock.me");

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
    private LongSerialization longSerialization = LongSerialization.getSerialization();

//    /**
//     * HashMap with an actual data.
//     */
//    private HashMap<K, V> data = new HashMap<>();

    /**
     * An absolute path for the filename with the database.
     */
    private final String absoluteStoragePath = "";
    private String keysFilename = absoluteStoragePath + File.separator + "keys.db";
    private String valueFilename = absoluteStoragePath + File.separator + "values.db";

    /**
     * Serializators for keys and values.
     */
    private StorageSerialization<K> keySerialization;
    private StorageSerialization<V> valueSerialization;

    /**
     * Random access file with our database
     */
    private RandomAccessFile keysStorage;
    private RandomAccessFile valuesStorage;

    /**
     * A buffer to handle most recent requests
     */
    private HashMap<K, V> recentsBuffer = new HashMap<K, V>();

    /**
     * A buffer for keys and offsets
     */
    private HashMap<K, Long> offsets = new HashMap<K, Long>();
    // лишнее тут


    /**
     * Path to the storage
     */
    private String path;

    public PerfomanceStorage(String pathPassed, StorageSerialization<K> passedKeySerialization,
                     StorageSerialization<V> passedValueSerialization) throws IOException {
        path = pathPassed;
//        if (!lockfile.createNewFile()) {
//            throw new IOException("Another process is working.");
//        }
        keySerialization = passedKeySerialization;
        valueSerialization = passedValueSerialization;
        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new IOException("Directory not found.");
        }
//        absoluteStoragePath = path + File.separator + keysFilename;
        keysFilename = path + File.separator + keysFilename;
        valueFilename = path + File.separator + valueFilename;
        File f = new File(keysFilename);
        if (!f.createNewFile()) {
//            file = new RandomAccessFile(f, "rw");
            loadData();
        } else {
//            file = new RandomAccessFile(f, "rw");
            newStorage();
        }
    }

    /**
     * Creating a storage
     */
    private void newStorage() {
        File keysFile = new File(keysFilename);
        File valuesFile = new File(valueFilename);
        try {
            keysFile.createNewFile();
            valuesFile.createNewFile();
            keysStorage = new RandomAccessFile(keysFile, "rw");
            valuesStorage = new RandomAccessFile(valuesFile, "rw");
        } catch (IOException e) {
            throw new RuntimeException("bajdnlkjdnakljemnd");
        }

    }

    /**
     * Loads data from file
     */
    private void loadData() throws IOException {
//        data.clear();
        offsets.clear();
        recentsBuffer.clear();
        File keysFile = new File(keysFilename);
        File valuesFile = new File(valueFilename);
        if (!keysFile.exists() || !valuesFile.exists()) {
            return;
            //THROW ERROR
        }
        keysStorage = new RandomAccessFile(keysFile, "rw");
        valuesStorage = new RandomAccessFile(valuesFile, "rw");

        int size = integerSerialization.read(keysStorage);
        for (int i = 0; i < size; i++) {
            K key = keySerialization.read(keysStorage);
            Long offset = longSerialization.read(keysStorage);
            offsets.put(key, offset);
        }
    }

    /**
     * Writes data to storage file using serializators
     */
    private void save() throws IOException {
//        file.seek(0);
//        // Clearing the contents of file before writing
//        file.setLength(0);
//        integerSerialization.write(file, data.size());
//        for (K key: data.keySet()) {
//            keySerialization.write(file, key);
//            valueSerialization.write(file, data.get(key));
//        }
//        file.close();
        keysStorage.seek(0);
        integerSerialization.write(keysStorage, offsets.size());
        for (K key: offsets.keySet()) {
            keySerialization.write(keysStorage, key);
            longSerialization.write(keysStorage, offsets.get(key));
        }
        keysStorage.close();
        valuesStorage.close();
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
        if (!exists(key)) {
//            throw new RuntimeException("not exists");
            return null;
        }
        V recent = recentsBuffer.get(key);
        if (recent != null) {
            return recent;
        }
        try {
            Long offset = offsets.get(key);
            valuesStorage.seek(offset);
            V value = valueSerialization.read(valuesStorage);
//            recentsBuffer.put(key, value);
            return value;
        } catch (IOException e) {
            throw new RuntimeException("flwkfnjksnfkjwnf");
        }
    }

    @Override
    public boolean exists(K key) {
        checkIfStorageIsClosed();
//        return data.keySet().contains(key);
        return offsets.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkIfStorageIsClosed();
        try {
            offsets.put(key, valuesStorage.length());
            valuesStorage.seek(valuesStorage.length());
            valueSerialization.write(valuesStorage, value);
        } catch (IOException excep) {
            System.out.println("все плохо");
        }
    }

    @Override
    public void delete(K key) {
        checkIfStorageIsClosed();
        if (exists(key)) {
            offsets.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
//        return data.keySet().iterator();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkIfStorageIsClosed();
//        return data.size();
        return offsets.size();
    }

    @Override
    public void close() throws IOException {
        checkIfStorageIsClosed();
        isStorageClosed = true;
        save();
//        file.close();
    }
}
