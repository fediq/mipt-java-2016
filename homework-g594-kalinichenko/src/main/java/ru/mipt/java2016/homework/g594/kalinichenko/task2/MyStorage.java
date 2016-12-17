package ru.mipt.java2016.homework.g594.kalinichenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by masya on 27.10.16.
 */

class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private MySerializer<Integer> lengthSerializer;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valSerializer;
    private HashMap<K, V> map;
    private File file;
    private boolean open = false;

    private void load() {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            long len = lengthSerializer.get(in);
            for (int i = 0; i < len; ++i) {
                K key = keySerializer.get(in);
                V val = valSerializer.get(in);
                map.put(key, val);
            }
            byte[] check = new byte[1]; ///check for EOF cause file might be longer
            if (in.read(check) != -1) {
                throw new IllegalStateException("Invalid file");
            }
            in.close();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }

    MyStorage(String path, MySerializer keyS, MySerializer valS) {
        //I can't make it public, it doesn't pass maven test...
        lengthSerializer = new MyIntSerializer();
        keySerializer = keyS;
        valSerializer = valS;
        map = new HashMap();
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalStateException("Wrong path to directory");
        }
        file = new File(path + "/storage.db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
            lengthSerializer.put(out, 0);
            try {
                out.close();
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with file");
            }
        }
        open = true;
        load();
    }

    private void checkExistence() {
        if (!open) {
            throw new IllegalStateException("Storage closed");
        }
    }

    @Override
    public V read(K key) {
        checkExistence();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkExistence();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkExistence();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkExistence();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkExistence();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() {
        open = false;
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        lengthSerializer.put(out, map.size());
        for (HashMap.Entry<K, V> elem: map.entrySet()) {
            keySerializer.put(out, elem.getKey());
            valSerializer.put(out, elem.getValue());
        }
        try {
            out.close();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}
