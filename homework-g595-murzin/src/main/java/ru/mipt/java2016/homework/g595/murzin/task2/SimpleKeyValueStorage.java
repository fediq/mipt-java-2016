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

    private static String VALIDATION_STRING = "This is a SimpleKeyValueStorage, v1.0";

    private HashMap<K, V> map = new HashMap<>();
    private File storage;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

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
                throw new RuntimeException("Storage file " + storage + " does not look like a SimpleKeyValueStorage");
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

    @Override
    public V read(K key) {
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        writeToStorage();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}