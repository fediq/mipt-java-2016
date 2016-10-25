package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";
    private RandomAccessFile file;
    private boolean isClosed = false;
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private MySerialization<Integer> intSerialization;
    private Map<K, V> objects = new TreeMap<K, V>();

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
            } catch (IOException error) {
                throw new ParseException("Invalid database", (int) file.getFilePointer());
            }
        }
    }

    public MyKeyValueStorage(String directoryName,
                             MySerialization<K> keySerializationIn,
                             MySerialization<V> valueSerializationIn) {
        try {
            keySerialization = keySerializationIn;
            valueSerialization = valueSerializationIn;
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V read(K key) {
        return objects.get(key);
    }

    @Override
    public boolean exists(K key) {
        return objects.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        objects.putIfAbsent(key, value);
    }

    @Override
    public void delete(K key) {
        objects.remove(key);
    }

    private class KVSIterator implements Iterator<K> {
        private Iterator<K> iterator;

        KVSIterator(Iterator<K> iter) {
            iterator = iter;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public K next() {
            return iterator.next();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        return new KVSIterator(objects.keySet().iterator());
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public void close() throws IOException {
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
