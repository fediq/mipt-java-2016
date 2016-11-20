package ru.mipt.java2016.homework.g595.ulyanin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ulyanin
 * @since 2016-10-29
 */


public class MapPreservingStorage<K, V> implements KeyValueStorage<K, V> {
    private enum StorageState { CLOSED, OPENED }

    private static final String DEFAULT_DB_NAME = ".storage.db";

    private static final String MD5_FILE_SUFFIX_NAME = ".hash";

    private static final String MD5_SALT = "theSaltYouWillNeverKnow";

    private static final String STORAGE_VALIDATE_STRING = "MapPreservingStorage";

    private final HashMap<K, V> storage = new HashMap<>();
    private StorageState state;
    private String storageFileName;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;

    MapPreservingStorage(String path, Serializer<K> kSerializer, Serializer<V> vSerializer)
            throws IOException, NoSuchAlgorithmException {
        keySerializer = kSerializer;
        valueSerializer = vSerializer;
        File tmp = new File(path);
        if (tmp.exists()) {
            if (tmp.isDirectory()) {
                path += "/" + DEFAULT_DB_NAME;
            }
        } else {
            throw new IllegalArgumentException("file " + path + " does not exist");
        }
        storageFileName = path;
        state = StorageState.OPENED;
        File target = new File(path);
        if (target.exists()) {
            readFromFile();
        }
    }

    private String getCheckSumFileName() {
        return storageFileName + MD5_FILE_SUFFIX_NAME;
    }

    private void readFromFile() throws IOException, NoSuchAlgorithmException {
        File file = new File(storageFileName);

        FileInputStream fileInputStream = new FileInputStream(file);

        DataInputStream dataIS = new DataInputStream(fileInputStream);
        if (!StringSerializer.getInstance().deserialize(dataIS).equals(STORAGE_VALIDATE_STRING)) {
            throw new IllegalArgumentException("It is not file of dataBase");
        }
        int size = IntegerSerializer.getInstance().deserialize(dataIS);
        for (int i = 0; i < size; ++i) {
            K key = keySerializer.deserialize(dataIS);
            V value = valueSerializer.deserialize(dataIS);
            write(key, value);
        }
        dataIS.close();
    }

    private void writeToFile() throws IOException, NoSuchAlgorithmException {
        File file = new File(storageFileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        DataOutputStream dataOS = new DataOutputStream(fileOutputStream);
        StringSerializer.getInstance().serialize(STORAGE_VALIDATE_STRING, dataOS);
        IntegerSerializer.getInstance().serialize(storage.size(), dataOS);
        for (HashMap.Entry<K, V> entry : storage.entrySet()) {
            keySerializer.serialize(entry.getKey(), dataOS);
            valueSerializer.serialize(entry.getValue(), dataOS);
        }
        dataOS.close();
    }

    private void throwIfClosed(String methodName) {
        if (state.equals(StorageState.CLOSED)) {
            throw new IllegalStateException("trying to apply method " + methodName + " to closed MapPreservingStorage");
        }
    }

    @Override
    public V read(K key) {
        throwIfClosed("read");
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        throwIfClosed("exist");
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        throwIfClosed("write");
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        throwIfClosed("delete");
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        throwIfClosed("readKeys");
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        throwIfClosed("size");
        return storage.size();
    }

    @Override
    public void close() throws IOException {
        try {
            writeToFile();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        state = StorageState.CLOSED;
        storage.clear();
    }
}
