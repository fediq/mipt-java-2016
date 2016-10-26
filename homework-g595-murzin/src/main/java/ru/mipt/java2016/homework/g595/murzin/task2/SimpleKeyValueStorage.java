package ru.mipt.java2016.homework.g595.murzin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Дмитрий Мурзин on 18.10.16.
 */
public class SimpleKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final String VALIDATION_STRING = "This is a SimpleKeyValueStorage, v1.0";

    private HashMap<K, V> map = new HashMap<>();
    private File storage;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isClosed;

    public SimpleKeyValueStorage(String path,
                                 SerializationStrategy<K> keySerializationStrategy,
                                 SerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File directory = new File(path);
        if (!directory.isDirectory() || !directory.exists()) {
            throw new RuntimeException("Path " + path + " is not a valid directory name");
        }
        storage = new File(directory, "storage.db");
        if (storage.exists()) {
            readFromStorage();
        }
    }

    private void readFromStorage() {
        try (DataInputStream input = new DataInputStream(new FileInputStream(storage))) {
            String validationString = input.readUTF();
            if (!VALIDATION_STRING.equals(validationString)) {
                throw new RuntimeException(String.format("Storage file %s does not look like a SimpleKeyValueStorage: "
                        + "expected '%s', got '%s'", storage, VALIDATION_STRING, validationString));
            }
            int n = input.readInt();
            for (int i = 0; i < n; i++) {
                K key = keySerializationStrategy.deserializeFromStream(input);
                V value = valueSerializationStrategy.deserializeFromStream(input);
                map.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from storage file " + storage, e);
        }
    }

    private void writeToStorage() throws IOException {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(storage))) {
            output.writeUTF(VALIDATION_STRING);
            output.writeInt(map.size());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                valueSerializationStrategy.serializeToStream(entry.getValue(), output);
            }
            output.close();
        } catch (IOException e) {
            throw new IOException("Can't write to storage file " + storage, e);
        }
    }

    private void checkForClosed() {
        if (isClosed) {
            throw new RuntimeException("Access to closed storage");
        }
    }

    @Override
    public V read(K key) {
        checkForClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkForClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkForClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkForClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkForClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkForClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        checkForClosed();
        writeToStorage();
        isClosed = true;
    }

    @Override
    public String toString() {
        checkForClosed();
        return map.toString();
    }
}