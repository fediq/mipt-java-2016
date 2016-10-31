package ru.mipt.java2016.homework.g597.smirnova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Elena Smirnova on 30.10.2016.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> data = new HashMap<>();
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private Boolean isOpen = false;
    private File storage;

    public MyKeyValueStorage(String path, SerializationStrategy<K> newKeySerializationStrategy,
                             SerializationStrategy<V> newValueSerializationStrategy) throws IOException {
        if ((new File(path)).exists()) {
            storage = new File(path, "storage.db");
            isOpen = true;
            keySerializationStrategy = newKeySerializationStrategy;
            valueSerializationStrategy = newValueSerializationStrategy;
            if (storage.exists()) {
                getData();
            }
        } else {
            throw new FileNotFoundException("No such directory");
        }
    }

    private void getData() throws IOException {
        try (DataInputStream input = new DataInputStream(new FileInputStream(storage))) {
            int size = input.readInt();
            for (int i = 0; i < size; i++) {
                K key = keySerializationStrategy.readFromStream(input);
                V value = valueSerializationStrategy.readFromStream(input);
                data.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public V read(K key) {
        if (isOpen) {
            return data.get(key);
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public boolean exists(K key) {
        if (isOpen) {
            return data.containsKey(key);
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public void write(K key, V value) {
        if (isOpen) {
            data.put(key, value);
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public void delete(K key) {
        if (isOpen) {
            data.remove(key);
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (isOpen) {
            return data.keySet().iterator();
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public int size() {
        if (isOpen) {
            return data.size();
        } else {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public void close() throws IllegalStateException {
        if (isOpen) {
            isOpen = false;
            try (DataOutputStream output = new DataOutputStream(new FileOutputStream(storage))) {
                output.writeInt(data.size());
                for (Map.Entry<K, V> entry : data.entrySet()) {
                    keySerializationStrategy.writeToStream(output, entry.getKey());
                    valueSerializationStrategy.writeToStream(output, entry.getValue());
                }
            } catch (IOException e) {
                throw new AssertionError("Error in writing into file");
            }
        } else {
            throw new IllegalStateException("File is closed");
        }
    }
}
