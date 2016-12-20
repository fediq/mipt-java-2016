package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataBase<K, V> implements KeyValueStorage<K, V> {
    private File dbFile;
    private Boolean checkOpen;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private Map<K, V> data = new HashMap<>();

    public DataBase(String path, Serializer<K> keySer,
                             Serializer<V> valueSer) throws IOException {
        File checkDir = new File(path);
        keySerializer = keySer;
        valueSerializer = valueSer;
        dbFile = new File(path, "db.txt");
        if (!checkDir.exists()) {
            throw new RuntimeException("DataBase: File not found");
        }
        if (!checkDir.isDirectory()) {
            throw new RuntimeException("DataBase: Wrong path");
        }
        checkOpen = true;
        if (dbFile.exists()) {
            try (DataInputStream in = new DataInputStream(new FileInputStream(dbFile))) {
                int cntElems = in.readInt();
                for (int i = 0; i < cntElems; ++i) {
                    data.put(keySerializer.deserializeRead(in), valueSerializer.deserializeRead(in));
                }
            } catch (IOException e) {
                throw new RuntimeException("DataBase: Can't use database");
            }
        } else {
            dbFile.createNewFile();
        }
    }
    
    @Override
    public V read(K key) {
        checkNotClosed();
        return data.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return data.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        data.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        data.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return data.keySet().iterator();
    }

    @Override
    public int size() {
        checkNotClosed();
        return data.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(dbFile))) {
            out.writeInt(data.size());
            for (HashMap.Entry<K, V> pair: data.entrySet()) {
                keySerializer.serializeWrite(pair.getKey(), out);
                valueSerializer.serializeWrite(pair.getValue(), out);
            }
            checkOpen = false;
            data.clear();
        } catch (IOException exp) {
            throw new RuntimeException("DataBase: Can't close");
        }
    }

    private void checkNotClosed() {
        if (!checkOpen) {
            throw new RuntimeException("DataBase: Already closed");
        }
    }

}