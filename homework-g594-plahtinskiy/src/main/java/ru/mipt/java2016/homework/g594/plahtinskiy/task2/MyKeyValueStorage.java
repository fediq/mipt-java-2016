package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by VadimPl on 29.10.16.
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map;
    private String name;
    private RandomAccessFile filedb;
    private Serialization<K> serializationKey;
    private Serialization<V> serializationValue;

    public MyKeyValueStorage(String path, Serialization<K> serializationKey,
                             Serialization<V> serializationValue) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory no exists");
        }

        map = new HashMap<>();
        this.name = "storage.db";
        this.serializationKey = serializationKey;
        this.serializationValue = serializationValue;

        String dbpath = path + File.separator + this.name;
        File database = new File(dbpath);
        if (!database.createNewFile()) {
            filedb = new RandomAccessFile(database, "rw");
            try {
                openDataBase();
            } catch (MyException e) {
                e.printStackTrace();
            }
        } else {
            filedb = new RandomAccessFile(database, "rw");
        }
    }

    private void openDataBase() throws MyException {
        Integer check = 0;
        try {
            filedb.seek(0);
            check = filedb.readInt();
        } catch (IOException e) {
            throw new MyException("No good");
        }
        if (check != 208) {
            throw new MyException("Not file for Data Base");
        }
        map.clear();
        try {
            long length = filedb.length();
            while (filedb.getFilePointer() < length) {
                K key;
                V value;
                key = serializationKey.read(filedb);
                value = serializationValue.read(filedb);
                if (map.containsKey(key)) {
                    throw new MyException("Duplicate key");
                } else {
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new MyException("No open file");
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {

        filedb.setLength(0);
        filedb.seek(0);
        filedb.writeInt(208);

        for (HashMap.Entry<K, V> pair: map.entrySet()) {
            serializationKey.write(filedb, pair.getKey());
            serializationValue.write(filedb, pair.getValue());
        }
        filedb.close();
        map.clear();
    }

    private void checkNotClosed() {
        if (map == null) {
            throw new IllegalStateException("Already closed");
        }
    }
}

