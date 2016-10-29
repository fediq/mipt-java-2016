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
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private IntegerSerializationStrategy integerSerializationStrategy;
    private String name;
    private RandomAccessFile file;
    private File lock;
    private HashMap<K, V> elements;

    public MyKeyValueStorage(String path, String name, SerializationStrategy<K> keySerializationStrategy,
                             SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory not exist");
        }

        String lockPath = path + File.separator + name + ".lock";
        lock = new File(lockPath);
        if (!lock.createNewFile()) {
            throw new IOException("Database already open");
        }

        elements = new HashMap<>();
        this.name = name;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        integerSerializationStrategy = IntegerSerializationStrategy.getInstance();

        String databasePath = path + File.separator + this.name;
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
        int size = integerSerializationStrategy.read(file);

        if (size < 0) {
            throw new IOException("Invalid database");
        }

        for (int i = 0; i < size; i++) {
            K key = keySerializationStrategy.read(file);
            V value = valueSerializationStrategy.read(file);
            if (elements.containsKey(key)) {
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

    @Override
    public Iterator<K> readKeys() {
        return elements.keySet().iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void close() throws IOException {
        file.setLength(0);
        file.seek(0);
        integerSerializationStrategy.write(file, size());
        for (Map.Entry<K, V> entry : elements.entrySet()) {
            keySerializationStrategy.write(file, entry.getKey());
            valueSerializationStrategy.write(file, entry.getValue());
        }
        elements.clear();
        file.close();
        Files.delete(lock.toPath());
    }
}
