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

    public MyKeyValueStorage(String nameDirectory, Identification<K> key, Identification<V> value) throws IOException {

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

    private void write() {
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            output.writeInt(hashmap.size());
            for (Map.Entry<K, V> entry : hashmap.entrySet()) {
                keyIdentification.write(output, entry.getKey());
                valueIdentification.write(output, entry.getValue());
            }
            output.close();
        } catch (IOException e) {
            throw new RuntimeException("Error write");
        }
    }

    private  void read() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            int readSize = input.readInt();
            int i = 0;
            while (i < readSize) {
                K key = keyIdentification.read(input);
                V value = valueIdentification.read(input);
                hashmap.put(key, value);
                i++;
            }
            input.close();
        } catch (IOException e) {
            throw new RuntimeException("Error read");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        return hashmap.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
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
        return hashmap.size();
    }

    @Override
    public void delete(K key) {
        hashmap.remove(key);
    }

    @Override
    public void write(K key, V value) {
        hashmap.put(key, value);
    }

    @Override
    public V read(K key) {
        return hashmap.get(key);
    }
}
