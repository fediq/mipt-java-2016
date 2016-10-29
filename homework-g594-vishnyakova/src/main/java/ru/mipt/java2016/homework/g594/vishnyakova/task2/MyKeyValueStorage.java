package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nina on 24.10.16.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private String fileName;
    private String type;
    private HashMap<K, V> map;
    private SerializationStrategy<K> keySerializator;
    private SerializationStrategy<V> valSerializator;
    private boolean opened;

    public MyKeyValueStorage(String typ, String path, SerializationStrategy sKey, SerializationStrategy sVal) {
        type = typ;
        fileName = path + "/store.txt";
        keySerializator = sKey;
        valSerializator = sVal;
        map = new HashMap<K, V>();
        opened = true;
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't create new file");
            }
            try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(fileName))) {
                wr.writeUTF(type);
                wr.writeInt(0);
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't write to file");
            }
        }

        try (DataInputStream rd = new DataInputStream(new FileInputStream(fileName))) {
            if (!rd.readUTF().equals(type)) {
                throw new IllegalStateException("Invalid file");
            }
            int number = rd.readInt();
            for (int i = 0; i < number; ++i) {
                K key = keySerializator.read(rd);
                V val = valSerializator.read(rd);
                map.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read from to file");
        }
    }

    @Override
    public V read(K key) {
        checkIfNotOpened();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkIfNotOpened();
        return map.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkIfNotOpened();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkIfNotOpened();
        map.remove(key);
    }

    @Override
    public Iterator readKeys() {
        checkIfNotOpened();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkIfNotOpened();
        return map.size();
    }

    @Override
    public void close() {
        checkIfNotOpened();
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(fileName))) {
            wr.writeUTF(type);
            wr.writeInt(map.size());
            for (Map.Entry<K, V> entry: map.entrySet()) {
                keySerializator.write(wr, entry.getKey());
                valSerializator.write(wr, entry.getValue());
            }
            opened = false;
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't write storage to file");
        }
    }

    private void checkIfNotOpened() {
        if (!opened) {
            throw new IllegalStateException("Couldn't work with closed storage");
        }
    }
}
