package ru.mipt.java2016.homework.g595.gusarova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Дарья on 29.10.2016.
 */
public class KVStorage<K, V> implements KeyValueStorage<K, V> {
    private File f;
    private SerializerAndDeserializer<K> serializerAndDeserializerForKey;
    private SerializerAndDeserializer<V> serializerAndDeserializerForValue;
    private HashMap<K, V> map;
    private Boolean baseClosed = false;

    private void addData() throws IOException {
        map = new HashMap<K, V>();
        DataInputStream input = new DataInputStream(new FileInputStream(f));
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            map.put(serializerAndDeserializerForKey.deserialize(input),
                    serializerAndDeserializerForValue.deserialize(input));
        }
        input.close();
    }

    public KVStorage(String path, SerializerAndDeserializer<K> forKey,
                     SerializerAndDeserializer<V> forValue) throws IOException {
        f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            throw new IOException("this path is incorrect");
        }
        f = new File(path + File.separator + "storage.txt");
        serializerAndDeserializerForKey = forKey;
        serializerAndDeserializerForValue = forValue;
        try {
            addData();
        } catch (IOException exp) {
            //base was never printed on disk
        }
    }


    @Override
    public V read(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.get(key);

    }

    @Override
    public boolean exists(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        return map.size();
    }

    @Override
    public void close() throws IOException {
        if (baseClosed) {
            throw new RuntimeException("base closed");
        }
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(f));
            output.writeInt(map.size());
            for (K entry : map.keySet()) {
                serializerAndDeserializerForKey.serialize(entry, output);
                serializerAndDeserializerForValue.serialize(map.get(entry), output);
            }
            output.close();
            baseClosed = true;
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }
}