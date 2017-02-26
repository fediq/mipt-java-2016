package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";
    private File file;
    private boolean isClosed = false;
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private MySerialization<Integer> intSerialization;
    private Map<K, V> objects = new HashMap<K, V>();

    private void update() throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        objects.clear();
        int cntValues;
        try {
            cntValues = intSerialization.deserialize(stream);
        } catch (IOException error) {
            throw new IOException("Invalid database");
        }
        if (cntValues < 0) {
            throw new IOException("Invalid database");
        }
        for (int q = 0; q < cntValues; ++q) {
            try {
                K key = keySerialization.deserialize(stream);
                V value = valueSerialization.deserialize(stream);
                objects.putIfAbsent(key, value);
            } catch (IOException error) {
                throw new IOException("Invalid database");
            }
        }
        stream.close();
    }

    public MyKeyValueStorage(String directoryName,
                             MySerialization<K> keySerializationInp,
                             MySerialization<V> valueSerializationInp) {
        keySerialization = keySerializationInp;
        valueSerialization = valueSerializationInp;
        intSerialization = new IntSerialization();

        try {
            if (Files.notExists(Paths.get(directoryName))) {
                throw new FileNotFoundException("directory not found");
            }
            file = new File(directoryName + File.separator + DB_NAME);
            if (!file.createNewFile()) {
                update();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error during opening database");
        }
    }

    private void checkIsNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Cannot access to closed database");
        }
    }

    @Override
    public V read(K key) {
        checkIsNotClosed();
        return objects.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkIsNotClosed();
        return objects.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkIsNotClosed();
        objects.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkIsNotClosed();
        objects.remove(key);
    }

    private class KVSIterator implements Iterator<K> {
        private Iterator<K> iterator;

        KVSIterator(Iterator<K> iter) {
            iterator = iter;
        }

        @Override
        public boolean hasNext() {
            checkIsNotClosed();
            return iterator.hasNext();
        }

        @Override
        public K next() {
            checkIsNotClosed();
            return iterator.next();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkIsNotClosed();
        return new KVSIterator(objects.keySet().iterator());
    }

    @Override
    public int size() {
        checkIsNotClosed();
        return objects.size();
    }

    @Override
    public void close() throws IOException {
        checkIsNotClosed();
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        intSerialization.serialize(stream, objects.size());
        for (Map.Entry<K, V> entry : objects.entrySet()) {
            keySerialization.serialize(stream, entry.getKey());
            valueSerialization.serialize(stream, entry.getValue());
        }

        isClosed = true;
        stream.close();
    }
}
