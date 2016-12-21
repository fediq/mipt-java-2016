package ru.mipt.java2016.homework.g599.derut.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MyKVStorage<K, V> implements KeyValueStorage<K, V> {
    private final RandomAccessFile dataBase;
    private final Map<K, V> data = new HashMap<>();
    private final Serializer<K> keyS;
    private final Serializer<V> valS;
    private boolean isClosed = true;
    private int length = 0;

    MyKVStorage(String path, Serializer<K> keyS, Serializer<V> valS) throws IOException {
        this.keyS = keyS;
        this.valS = valS;
        Path directPath = Paths.get(path, "dataBase.db");
        dataBase = new RandomAccessFile(directPath.toFile(), "rw");
        isClosed = false;
        if (dataBase.length() != 0) {
            initialRead();
        }
    }

    private void initialRead() throws IOException {
        length = dataBase.readInt();
        for (int i = 0; i < length; ++i) {
            K key = keyS.read(dataBase);
            V val = valS.read(dataBase);
            data.put(key, val);
        }
    }

    @Override
    public synchronized V read(K key) {
        checker();
        return data.get(key);
    }

    @Override
    public synchronized boolean exists(K key) {
        checker();
        return data.containsKey(key);

    }

    @Override
    public synchronized void write(K key, V value) {
        checker();
        if (!exists(key)) {
            ++length;
        }
         data.put(key, value);

    }

    @Override
    public synchronized void delete(K key) {
        checker();
        if (exists(key)) {
            data.remove(key);
            --length;
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checker();
        return data.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        checker();
        return length;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!isClosed) {
            render();
        }

    }

    public void render() throws IOException {
        dataBase.seek(0);
        dataBase.setLength(0);
        IntegerRead intR = new IntegerRead();
        intR.write(dataBase, length);
        for (Map.Entry<K, V> entry : data.entrySet()) {
            keyS.write(dataBase, entry.getKey());
            valS.write(dataBase, entry.getValue());
        }
        dataBase.close();
        isClosed = true;
    }

    private void checker() {
        if (isClosed) {
            throw new RuntimeException("Closed!!!");
        }
    }
}
