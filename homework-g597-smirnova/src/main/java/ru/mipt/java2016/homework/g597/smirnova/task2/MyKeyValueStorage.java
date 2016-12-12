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
    private File lock;

    public MyKeyValueStorage(String path, SerializationStrategy<K> newKeySerializationStrategy,
                             SerializationStrategy<V> newValueSerializationStrategy) throws IOException {
        if (!(new File(path)).exists()) {
            throw new FileNotFoundException("No such directory");
        }
        lock = new File(path, "lock.txt");
        if (!lock.createNewFile()) {
            throw new IllegalStateException("Lock has been set");
        }
        storage = new File(path, "storage.db");
        isOpen = true;
        keySerializationStrategy = newKeySerializationStrategy;
        valueSerializationStrategy = newValueSerializationStrategy;
        try {
            if (!storage.createNewFile()) {
                getData();
            }
        } catch (Exception e) {
            lock.delete();
            throw new IllegalStateException("Can't create file");
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
        } catch (Exception e) {
            lock.delete();
            throw new IllegalStateException("Can't read from file");
        }

    }

    private void checkForClosed() {
        if (!isOpen) {
            throw new IllegalStateException("File is closed");
        }
    }

    @Override
    public V read(K key) {
        checkForClosed();
        return data.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkForClosed();
        return data.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkForClosed();
        data.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkForClosed();
        data.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkForClosed();
        return data.keySet().iterator();
    }

    @Override
    public int size() {
        checkForClosed();
        return data.size();
    }

    @Override
    public void close() throws IllegalStateException {
        checkForClosed();
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(storage))) {
            output.writeInt(data.size());
            for (Map.Entry<K, V> entry : data.entrySet()) {
                keySerializationStrategy.writeToStream(output, entry.getKey());
                valueSerializationStrategy.writeToStream(output, entry.getValue());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error in writing into file");
        } finally {
            lock.delete();
            isOpen = false;
        }
    }
}
