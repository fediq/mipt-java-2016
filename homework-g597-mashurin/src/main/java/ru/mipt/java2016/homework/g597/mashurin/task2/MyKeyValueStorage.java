package ru.mipt.java2016.homework.g597.mashurin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.io.IOException;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private Identification<K> keyIdentification;
    private Identification<V> valueIdentification;
    private File file;
    private HashMap<K, V> hashmap;
    private boolean closedStreem;
    private File security;

    public MyKeyValueStorage(String nameDirectory, Identification<K> key, Identification<V> value) throws IOException {

        security = new File(nameDirectory, "security.db");
        if (!security.createNewFile()) {
            throw new IOException("File exists!");
        }

        hashmap = new HashMap<K, V>();
        File directory = new File(nameDirectory);
        keyIdentification = key;
        valueIdentification = value;
        if (!directory.isDirectory()) {
            throw new IOException("Isnt directory");
        }
        file = new File(nameDirectory, "storage.db");
        if (file.exists()) {
            read();
        }
    }

    private  void read() {
        try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
            int readSize = input.readInt();
            for (int i = 0; i < readSize; i++) {
                K key = keyIdentification.read(input);
                V value = valueIdentification.read(input);
                hashmap.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error read");
        }
    }

    private void write() {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(file))) {
            output.writeInt(hashmap.size());
            for (Map.Entry<K, V> entry : hashmap.entrySet()) {
                keyIdentification.write(output, entry.getKey());
                valueIdentification.write(output, entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error write");
        } finally {
            security.delete();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        return hashmap.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        return hashmap.containsKey(key);
    }

    @Override
    public void close() throws IOException {
        if (closedStreem) {
            throw new IOException("Streem closed");
        }
        write();
        closedStreem = true;
    }

    @Override
    public int size() {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        return hashmap.size();
    }

    @Override
    public void delete(K key) {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        hashmap.remove(key);
    }

    @Override
    public void write(K key, V value) {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        hashmap.put(key, value);
    }

    @Override
    public V read(K key) {
        if (closedStreem) {
            throw new IllegalStateException("Streem closed");
        }
        return hashmap.get(key);
    }
}
