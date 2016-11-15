package ru.mipt.java2016.homework.g596.stepanova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> database = new HashMap<>();
    private File file;
    private boolean isClosed;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategy,
            SerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File directory = new File(path);
        if (!directory.isDirectory() || !directory.exists()) {
            throw new RuntimeException("Valid directory name");
        }

        file = new File(directory, "storage.db");
        if (file.exists()) {
            readFromFile();
        }
    }

    private void readFromFile() {
        try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
            int databaseSize = input.readInt();
            for (int i = 0; i < databaseSize; i++) {
                K key = keySerializationStrategy.deserializeFromFile(input);
                V value = valueSerializationStrategy.deserializeFromFile(input);
                database.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from file");
        }
    }

    private void writeToFile() throws IOException {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(file))) {
            output.writeInt(database.size());
            for (Map.Entry<K, V> entry : database.entrySet()) {
                keySerializationStrategy.serializeToFile(entry.getKey(), output);
                valueSerializationStrategy.serializeToFile(entry.getValue(), output);
            }
        } catch (IOException e) {
            throw new IOException("Can't write to file");
        }
    }

    private void checkState() {
        if (isClosed) {
            throw new RuntimeException("Access to closed file");
        }
    }

    @Override
    public V read(K key) {
        checkState();
        return database.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkState();
        return database.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkState();
        database.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkState();
        database.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkState();
        return database.keySet().iterator();
    }

    @Override
    public int size() {
        checkState();
        return database.size();
    }

    @Override
    public void close() throws IOException {
        checkState();
        writeToFile();
        isClosed = true;
        database.clear();
    }
}