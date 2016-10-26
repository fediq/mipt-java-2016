package ru.mipt.java2016.homework.g597.moiseev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Дисковое хранилище.
 *
 * @author Fedor Moiseev
 * @since 26.10.16
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;
    private IntegerSerialization integerSerialization;
    private String name;
    private RandomAccessFile file;
    private File lock;
    private HashMap<K, V> elements;

    public MyKeyValueStorage(String path, String name, SerializationStrategy<K> keySerialization,
                             SerializationStrategy<V> valueSerialization) throws IOException{
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory not exist");
        }

        String lockPath = path + "/" + name + ".lock";
        File lock = new File(lockPath);
        if (!lock.createNewFile()) {
            throw new IOException("Database already open");
        }

        elements = new HashMap();
        this.name = name;
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        integerSerialization = IntegerSerialization.getInstance();

        String databasePath = path + "/" + this.name;
        File database = new File(databasePath);

        if (!database.createNewFile()) {
            file = new RandomAccessFile(database, "rw");
            loadFromFile();
        } else {
            file = new RandomAccessFile(database, "rw");
        }
    }

    private void loadFromFile() throws IOException {
        file.seek(0);
        elements.clear();
        int size = integerSerialization.read(file);

        if(size < 0) {
            throw new IOException("Invalid database");
        }

        for(int i = 0; i < size; i++) {
            K key = keySerialization.read(file);
            V value = valueSerialization.read(file);
            if(elements.containsKey(key)) {
                throw new IOException("Invalid database");
            } else {
                elements.put(key, value);
            }
        }
    }

    @Override
    public V read(K key) {
        return elements.get(key);
    }

    @Override
    public boolean exists(K key) {
        return elements.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        elements.put(key, value);
    }

    @Override
    public void delete(K key) {
        elements.remove(key);
    }

    private class KeyIterator implements Iterator<K> {
        private Iterator<K> iterator;

        public KeyIterator(Iterator<K> iterator) {
            this.iterator = iterator;
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
        return new KeyIterator(elements.keySet().iterator());
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void close() throws IOException {
        file.setLength(0);
        file.seek(0);
        integerSerialization.write(file, size());
        for(Map.Entry<K, V> entry : elements.entrySet()) {
            keySerialization.write(file, entry.getKey());
            valueSerialization.write(file, entry.getValue());
        }
        elements.clear();
        file.close();
        Files.delete(lock.toPath());
    }
}
