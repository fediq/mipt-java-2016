package ru.mipt.java2016.homework.g597.spirin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by whoami on 10/30/16.
 */
class PersistentKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private RandomAccessFile file;
    private FileChannel channel;
    private FileLock lock;

    private HashMap<K, V> records;

    PersistentKeyValueStorage(String path, String name,
                              SerializationStrategy<K> keySerializer,
                              SerializationStrategy<V> valueSerializer) throws IOException {

        handleFileExistence(path);

        file = new RandomAccessFile(path + File.separator + name, "rw");
        channel = file.getChannel();
        lock = channel.lock();

        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        records = new HashMap<>();

        loadData();
    }

    private void handleFileExistence(String path) throws FileNotFoundException {
        if (!Files.exists(Paths.get(path))) {
            throw new FileNotFoundException("Passed path is not valid.");
        }
    }

    private void loadData() throws IOException {
        file.seek(0);
        records.clear();

        while (file.getFilePointer() < file.length()) {
            K key = keySerializer.read(file);
            V value = valueSerializer.read(file);

            if (records.containsKey(key)) {
                throw new IOException("Ambiguous map entry.");
            } else {
                records.put(key, value);
            }
        }

    }

    @Override
    public Iterator<K> readKeys() {
        return records.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        return records.containsKey(key);
    }

    @Override
    public void close() throws IOException {
        file.setLength(0);
        file.seek(0);

        for (Map.Entry<K, V> entry : records.entrySet()) {
            keySerializer.write(file, entry.getKey());
            valueSerializer.write(file, entry.getValue());
        }

        lock.release();
        channel.close();
        records.clear();
        file.close();
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public void delete(K key) {
        records.remove(key);
    }

    @Override
    public V read(K key) {
        return records.get(key);
    }

    @Override
    public void write(K key, V value) {
        records.put(key, value);
    }
}
