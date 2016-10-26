package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";
    private RandomAccessFile file;
    private boolean isClosed = false;
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private MySerialization<Integer> intSerialization;
    private Map<K, V> objects = new HashMap<K, V>();

    private void update() throws IOException, ParseException {
        file.seek(0);
        objects.clear();
        int cntValues;
        try {
            cntValues = intSerialization.deserialize(file);
        } catch (IOException error) {
            throw new ParseException("Invalid database", (int) file.getFilePointer());
        }
        if (cntValues < 0) {
            throw new ParseException("Invalid database", (int) file.getFilePointer());
        }
        for (int q = 0; q < cntValues; ++q) {
            try {
                K key = keySerialization.deserialize(file);
                V value = valueSerialization.deserialize(file);
                objects.putIfAbsent(key, value);
            } catch (IOException error) {
                throw new ParseException("Invalid database", (int) file.getFilePointer());
            }
        }
    }

    public MyKeyValueStorage(String directoryName,
                             MySerialization<K> keySerializationInp,
                             MySerialization<V> valueSerializationInp) {
        try {
            keySerialization = keySerializationInp;
            valueSerialization = valueSerializationInp;
            intSerialization = new IntSerialization();

            if (Files.notExists(Paths.get(directoryName))) {
                throw new FileNotFoundException("directory not found");
            }
            File dbFile = new File(directoryName + "/" + DB_NAME);
            boolean isNewFile = dbFile.createNewFile();
            file = new RandomAccessFile(dbFile, "rw");
            if (!isNewFile) {
                update();
            }
        } catch (IOException | ParseException e) {
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
        private MyKeyValueStorage kvs;

        KVSIterator(Iterator<K> iter, MyKeyValueStorage myKvs) {
            kvs = myKvs;
            iterator = iter;
        }

        @Override
        public boolean hasNext() {
            kvs.checkIsNotClosed();
            return iterator.hasNext();
        }

        @Override
        public K next() {
            kvs.checkIsNotClosed();
            return iterator.next();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkIsNotClosed();
        return new KVSIterator(objects.keySet().iterator(), this);
    }

    @Override
    public int size() {
        checkIsNotClosed();
        return objects.size();
    }

    @Override
    public void close() throws IOException {
        checkIsNotClosed();
        file.setLength(0);
        file.seek(0);
        intSerialization.serialize(file, objects.size());
        for (Map.Entry<K, V> entry : objects.entrySet()) {
            keySerialization.serialize(file, entry.getKey());
            valueSerialization.serialize(file, entry.getValue());
        }

        isClosed = true;
        file.close();
    }
}
