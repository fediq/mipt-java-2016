package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class KlabertancOptimizedStorage<K, V> implements KeyValueStorage<K, V> {


    private static final String TRY_LOCK = "trylock";
    private static final String STORAGE_FILE_NAME = "KlabertancStorage";
    private static final String KEYS_FILE_NAME = STORAGE_FILE_NAME + "Keys";
    private static final String VALUES_FILE_NAME = STORAGE_FILE_NAME + "Values";

    private HashMap<K, Long> keyAndOffset = new HashMap<>();
    private String directory;
    private Serialization<K> keySerialization;
    private Serialization<V> valueSerialization;
    private RandomAccessFile keys;
    private RandomAccessFile values;
    private File lockAccess;
    private File storageFile;
    private boolean flagForClose;

    public void almostInitStorage() throws IOException {
        File keyFile = new File(KEYS_FILE_NAME);
        File valFile = new File(VALUES_FILE_NAME);

        keyFile.createNewFile();
        valFile.createNewFile();

        keys = new RandomAccessFile(keyFile, "rw");
        values = new RandomAccessFile(valFile, "rw");
    }

    public void openStorage() throws IOException {
        File keyFile = new File(KEYS_FILE_NAME);
        File valFile = new File(VALUES_FILE_NAME);

        if (!keyFile.exists() || !valFile.exists()) {
            throw new IOException("Some files are missing!");
        }

        keys = new RandomAccessFile(keyFile, "rw");
        values = new RandomAccessFile(valFile, "rw");

        getKeyOffset();
    }

    public void getKeyOffset() throws IOException {
        int keysNumber = keys.readInt();
        for (int i = 0; i < keysNumber; ++i) {
            K key = keySerialization.read(keys);
            Long offset = keys.readLong();
            keyAndOffset.put(key, offset);
        }
    }

    public KlabertancOptimizedStorage(String path, Serialization<K> k, Serialization<V> v) throws
            IOException {

        directory = path;
        keySerialization = k;
        valueSerialization = v;

        lockAccess = new File(directory, TRY_LOCK);
        if (lockAccess.exists()) {
            throw new RuntimeException("Another process is already running!");
        }
        lockAccess.mkdir();

        File receivedFile = new File(directory);
        if (receivedFile.exists() && receivedFile.isDirectory()) {
            storageFile = new File(directory + File.separator + storageFile);
        } else {
            throw new RuntimeException("path" + directory + " is not valid!");
        }

        if (storageFile.exists()) {
            DataInputStream input = new DataInputStream(new FileInputStream(storageFile));
            openStorage();
        } else {
            storageFile.createNewFile();
            almostInitStorage();
        }

        flagForClose = false;
    }

    private void putStorageOnDisk() throws IOException {

        keys.seek(0);
        keys.writeInt(keyAndOffset.size());
        for (K key : keyAndOffset.keySet()) {
            keySerialization.write(keys, key);
            keys.writeLong(keyAndOffset.get(key));
        }

        keyAndOffset.clear();

        keys.close();
        values.close();

        flagForClose = true;
    }

    private void isStorageClosed() {
        if (flagForClose) {
            throw new RuntimeException("You're a bad guy. Don't try to access the closed storage!");
        }
    }

    @Override
    public V read(K key) {
        isStorageClosed();

        if (!keyAndOffset.containsKey(key)) {
            return null;
        }

        Long offset = keyAndOffset.get(key);
        try {
            values.seek(offset);
            return valueSerialization.read(values);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        isStorageClosed();

        return keyAndOffset.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isStorageClosed();

        try {
            keyAndOffset.put(key, values.length());
            values.seek(values.length());
            valueSerialization.write(values, value);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        isStorageClosed();

        keyAndOffset.remove(key);
    }


    @Override
    public Iterator<K> readKeys() {
        isStorageClosed();

        return keyAndOffset.keySet().iterator();
    }

    @Override
    public int size() {
        return keyAndOffset.size();
    }

    @Override
    public void close() throws IOException {
        isStorageClosed();

        putStorageOnDisk();
        lockAccess.delete();
    }
}
