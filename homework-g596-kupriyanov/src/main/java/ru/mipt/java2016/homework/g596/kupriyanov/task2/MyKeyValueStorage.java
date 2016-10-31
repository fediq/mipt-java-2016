package ru.mipt.java2016.homework.g596.kupriyanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

import java.io.IOException;

/**
 * Created by Artem Kupriyanov on 29/10/2016.
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private File file;
    private Boolean dbOpen;
    private SerializationAndDeserializationStrategy<K> sADForKey;
    private SerializationAndDeserializationStrategy<V> sADForValue;
    private HashMap<K, V> db = new HashMap<K, V>();

    public MyKeyValueStorage(String path, SerializationAndDeserializationStrategy<K> sadForKey,
                     SerializationAndDeserializationStrategy<V> sadForValue) throws IOException {
        File checkDir = new File(path);
        sADForKey = sadForKey;
        sADForValue = sadForValue;
        file = new File(path + "/db.txt");
        if (!checkDir.exists()) {
            throw new RuntimeException("FILE DOESN'T EXIST");
        }
        if (!checkDir.isDirectory()) {
            throw new RuntimeException("NO DIRECTORY");
        }
        dbOpen = true;
        try {
            FileInputStream fin = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fin);
            int cntElems = in.readInt();
            for (int i = 0; i < cntElems; ++i) {
                db.put(sADForKey.read(in), sADForValue.read(in));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V read(K key) {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T READ");
        }
        return db.get(key);
    }

    @Override
    public boolean exists(K key) {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T GET EXISTS");
        }
        return db.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T WRITE");
        }
        db.put(key, value);
    }

    @Override
    public void delete(K key) {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T DELETE");
        }
        db.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T GET ITERATOR");
        }
        return db.keySet().iterator();
    }

    @Override
    public int size() {
        if (!dbOpen) {
            throw new RuntimeException("COLDN'T GET SIZE");
        }
        return db.size();
    }

    @Override
    public void close() throws IOException {
        if (!dbOpen) {
            throw new RuntimeException("COULDN'T CLOSE DB");
        }
        try {
            FileOutputStream fout = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fout);
            out.writeInt(db.size());
            for (HashMap.Entry<K, V> pair: db.entrySet()) {
                sADForKey.write(pair.getKey(), out);
                sADForValue.write(pair.getValue(), out);
            }
            dbOpen = false;
            out.close();
            db.clear();
        } catch (IOException exp) {
            throw new RuntimeException("COULDN'T CLOSE DB");
        }
    }
}