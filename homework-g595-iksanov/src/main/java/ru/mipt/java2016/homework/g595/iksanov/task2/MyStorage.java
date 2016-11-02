package ru.mipt.java2016.homework.g595.iksanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * KeyValueStorage.
 * Created by Эмиль Иксанов.
 */
public class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> myMap = new HashMap<>();
    private File workfile;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isClosed;

    public MyStorage(String path, SerializationStrategy<K> keySerializationStrategy,
                     SerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += File.separator + "storage.db";
        }
        workfile = new File(path);
        if (workfile.exists()) {
            try (DataInputStream input = new DataInputStream(new FileInputStream(workfile))) {
                int n = input.readInt();
                for (int i = 0; i < n; i++) {
                    K key = keySerializationStrategy.read(input);
                    V value = valueSerializationStrategy.read(input);
                    myMap.put(key, value);
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading from file problem");
            }
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return myMap.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return myMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        myMap.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        myMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return myMap.keySet().iterator();
    }

    @Override
    public int size() {
        return myMap.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(workfile))) {
            output.writeInt(myMap.size());
            for (Map.Entry<K, V> entry : myMap.entrySet()) {
                keySerializationStrategy.write(entry.getKey(), output);
                valueSerializationStrategy.write(entry.getValue(), output);
            }
            output.close();
        } catch (IOException e) {
            throw new IOException("Writing to file error");
        }
        isClosed = true;
        myMap.clear();
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Closed File");
        }
    }
}
