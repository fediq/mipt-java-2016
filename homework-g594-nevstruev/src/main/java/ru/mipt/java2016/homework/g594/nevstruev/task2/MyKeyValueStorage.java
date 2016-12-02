package ru.mipt.java2016.homework.g594.nevstruev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Владислав on 30.10.2016.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> storage;
    private Integer type;
    private Serialize<K> keySerialize;
    private Serialize<V> valueSerialize;
    private File file;
    private boolean opened;

    public MyKeyValueStorage(Integer newType, String path, Serialize typeKey, Serialize typeValue) {
        type = newType;
        keySerialize = typeKey;
        valueSerialize = typeValue;
        file = new File(path + "/input.txt");
        opened = true;
        storage = new HashMap<>();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Can not create the file\n");
            }
            PrintWriter output = null;
            try {
                output = new PrintWriter(new FileWriter(file));
            } catch (IOException e) {
                throw new IllegalStateException("Can not open file\n");
            }
            output.println(type.toString());
            output.println("0");
            output.close();
        }
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can not open the file\n");
        }
        Integer fileType = null;
        try {
            fileType = Integer.parseInt(input.readLine());
            if (fileType != type) {
                throw new IllegalStateException("It is not my file\n");
            }
            Integer size = Integer.parseInt(input.readLine());
            for (int i = 0; i < size; ++i) {
                K key = keySerialize.read(input);
                V value = valueSerialize.read(input);
                storage.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can not read of the file\n");
        }
        try {
            input.close();
        } catch (IOException e) {
            throw new IllegalStateException("Can not close the file\n");
        }
    }

    @Override
    public V read(K key) {
        isOpened();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        isOpened();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isOpened();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        isOpened();
        storage.remove(key);
    }

    @Override
    public Iterator readKeys() {
        isOpened();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        isOpened();
        return storage.size();
    }

    @Override
    public void close() {
        isOpened();
        try (PrintWriter output = new PrintWriter(new FileWriter(file))) {
            output.println(type.toString());
            output.println((Integer.valueOf(storage.size())).toString());
            for (Map.Entry<K, V> entry : storage.entrySet()) {
                keySerialize.write(output, entry.getKey());
                valueSerialize.write(output, entry.getValue());
            }
            opened = false;
            output.close();
        } catch (IOException e) {
            throw new IllegalStateException("Can not close the file\n");
        }
    }

    private void isOpened() {
        if (!opened) {
            throw new IllegalStateException("File is'nt open\n");
        }
    }
}
