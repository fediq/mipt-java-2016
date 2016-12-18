package ru.mipt.java2016.homework.g594.krokhalev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static java.io.File.separatorChar;

public class KrokhalevsKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    static final int CACHE_SIZE = 1000;

    private static final String RUN_NAME = "RUN";
    private static final String STORAGE_NAME = "storage.bd";
    private static final String STORAGE_TABLE = "storageTable.bd";

    private PartsController<K, V> partsController;

    private File workDirectory;
    private File runFile;

    KrokhalevsKeyValueStorage(String workDirectoryName,
                              SerializationStrategy<K> keySerializer,
                              SerializationStrategy<V> valueSerializer) {

        workDirectory = new File(workDirectoryName);
        StorageReader<K, V> storageReader = new StorageReader<K, V>(keySerializer, valueSerializer);

        if (!workDirectory.exists() || !workDirectory.isDirectory()) {
            throw new RuntimeException("Bad directory \"" + workDirectory.getAbsolutePath() + "\"");
        }

        try {
            if (!tryToRun()) {
                throw new RuntimeException("Can not run again in directory \"" + workDirectoryName + "\"");
            }

            partsController = new PartsController<K, V>(findStorage(), findStorageTable(), storageReader);

        } catch (IOException e) {
            throw new RuntimeException("Bad directory \"" + workDirectory.getAbsolutePath() + "\"");
        }
    }

    @Override
    public V read(K key) {
        if (runFile == null) {
            throw new RuntimeException("Storage is closed");
        }
        try {
            return partsController.getValue(key);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean exists(K key) {
        return partsController.isExistKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (runFile == null) {
            throw new RuntimeException("Storage is closed");
        }
        try {
            partsController.setValue(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        if (runFile == null) {
            throw new RuntimeException("Storage is closed");
        }
        try {
            partsController.deleteKey(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Iterator<K> readKeys() {
        if (runFile == null) {
            throw new RuntimeException("Storage is closed");
        }
        return partsController.getKeyIterator();
    }

    @Override
    public int size() {
        if (runFile == null) {
            throw new RuntimeException("Storage is closed");
        }
        return partsController.getCountKeys();
    }

    @Override
    public void close() throws IOException {
        partsController.close();
        if (!runFile.delete()) {
            throw new IOException("Can not delete run");
        }
        runFile = null;
    }

    private boolean tryToRun() throws IOException {
        runFile = new File(workDirectory.getAbsolutePath() + separatorChar + RUN_NAME);
        return runFile.createNewFile();
    }

    private File findStorage() throws IOException {
        for (File item : workDirectory.listFiles()) {
            if (item.isFile() && item.getName().equals(STORAGE_NAME)) {
                return item;
            }
        }
        File ssTable = new File(workDirectory.getAbsolutePath() + separatorChar + STORAGE_NAME);

        if (!ssTable.createNewFile()) {
            throw new IOException();
        }

        return ssTable;
    }

    private File findStorageTable() throws IOException {
        for (File item : workDirectory.listFiles()) {
            if (item.isFile() && item.getName().equals(STORAGE_TABLE)) {
                return item;
            }
        }
        File ssTable = new File(workDirectory.getAbsolutePath() + separatorChar + STORAGE_TABLE);

        if (!ssTable.createNewFile()) {
            throw new IOException();
        }

        return ssTable;
    }

    private boolean checkStorage(File ssTable) {
        return true;
    }

}
