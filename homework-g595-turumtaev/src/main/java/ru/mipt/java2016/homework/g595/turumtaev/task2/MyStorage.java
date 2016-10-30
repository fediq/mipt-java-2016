package ru.mipt.java2016.homework.g595.turumtaev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by galim on 27.10.2016.
 */

public class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> myMap = new HashMap<>();
    private File file;
    private MySerializationStrategy<K> keySerializationStrategy;
    private MySerializationStrategy<V> valueSerializationStrategy;
    private boolean isClosed;

    public MyStorage(String path, MySerializationStrategy<K> keySerializationStrategy,
                     MySerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += "/file.trm";
        }
        file = new File(path);
        if (file.exists()) {
            try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
                int n = input.readInt();
                for (int i = 0; i < n; i++) {
                    K key = keySerializationStrategy.read(input);
                    V value = valueSerializationStrategy.read(input);
                    myMap.put(key, value);
                }
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
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
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(file))) {
            output.writeInt(myMap.size());
            for (Map.Entry<K, V> entry : myMap.entrySet()) {
                keySerializationStrategy.write(entry.getKey(), output);
                valueSerializationStrategy.write(entry.getValue(), output);
            }
            output.close();
        } catch (IOException e) {
            throw new IOException("Can not write to file ");
        }
        isClosed = true;
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new RuntimeException("Closed File");
        }
    }
}