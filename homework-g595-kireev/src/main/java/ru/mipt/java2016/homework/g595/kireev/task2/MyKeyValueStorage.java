package ru.mipt.java2016.homework.g595.kireev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Карим on 25.10.2016.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final String dataName = "/storage.db";
    private HashMap<K, V> chache = new HashMap<K, V>();
    private MyBinaryHandler<K> keyHandler;
    private MyBinaryHandler<V> valueHandler;
    private MyBinaryHandler<Integer> lengthHandler;
    private String path;
    private boolean isClosed = false;

    MyKeyValueStorage(String keyType, String valueType, String path) throws IOException {
        this.path = path;
        keyHandler = new MyBinaryHandler<K>(keyType);
        valueHandler = new MyBinaryHandler<V>(valueType);
        lengthHandler = new MyBinaryHandler<Integer>("Integer");

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        takeChacheFromFile();
    }

    @Override
    public V read(K key) {
        checkClose();
        return chache.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkClose();
        return chache.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkClose();
        chache.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkClose();
        chache.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClose();
        return chache.keySet().iterator();
    }

    @Override
    public int size() {
        checkClose();
        return chache.size();
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        writeToFile();
    }

    private void checkClose() {
        if (isClosed) {
            throw new RuntimeException("Operation after closing");
        }
    }

    private void writeToFile() throws IOException {
        FileOutputStream out = new FileOutputStream(path + dataName);
        lengthHandler.putToOutput(out, chache.size());
        for (Map.Entry entry : chache.entrySet()) {
            keyHandler.putToOutput(out, (K) entry.getKey());
            valueHandler.putToOutput(out, (V) entry.getValue());
        }
        out.close();
    }

    private void takeChacheFromFile() throws IOException {
        File inFile = new File(path + dataName);
        if (!inFile.exists()) {
            inFile.createNewFile();
        }
        FileInputStream in = new FileInputStream(path + dataName);
        if (!chache.isEmpty()) {
            chache.clear();
        }
        Integer n;
        if (in.available() == 0) {
            n = 0;
        } else {
            n = lengthHandler.getFromInput(in);
        }
        for (int i = 0; i < n; ++i) {
            chache.put(keyHandler.getFromInput(in),
                    valueHandler.getFromInput(in));
        }
        in.close();
    }
}
