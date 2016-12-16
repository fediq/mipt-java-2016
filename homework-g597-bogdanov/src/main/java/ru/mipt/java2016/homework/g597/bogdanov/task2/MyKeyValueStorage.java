package ru.mipt.java2016.homework.g597.bogdanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private HashMap<K, V> contents;
    private final SerializationStrategy<K, V> serializationStrategy;
    private final RandomAccessFile file;
    private final String fileName;
    private final File lock;

    public MyKeyValueStorage(String path, String fileName,
                             SerializationStrategy<K, V> serializationStrategy) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory");
        }
        lock = new File(path + File.separator + fileName + ".lock");
        if (!lock.createNewFile()) {
            throw new IOException("Database is already open");
        }

        this.fileName = fileName;
        contents = new HashMap<>();
        this.serializationStrategy = serializationStrategy;

        File tmpFile = new File(path + File.separator + this.fileName);
        file = new RandomAccessFile(tmpFile, "rw");
        if (!tmpFile.createNewFile()) {
            readFromFile();
        }
    }

    private void readFromFile() throws IOException {
        file.seek(0);
        long fileLen = file.length();
        while (file.getFilePointer() < fileLen) {
            K key = serializationStrategy.readKey(file);
            if (contents.containsKey(key)) {
                throw new IOException("Duplicates are not allowed");
            } else {
                contents.put(key, serializationStrategy.readValue(file));
            }
        }
    }

    @Override
    public V read(K key) {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        return contents.get(key);
    }

    @Override
    public boolean exists(K key) {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        return contents.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        contents.put(key, value);
    }

    @Override
    public void delete(K key) {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        contents.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        return contents.keySet().iterator();
    }

    @Override
    public int size() {
        if (contents == null) {
            throw new IllegalStateException("Something went wrong");
        }
        return contents.size();
    }

    @Override
    public void close() throws IOException {
        try {
            file.seek(0);
            file.setLength(0);
            for (HashMap.Entry<K, V> entry : contents.entrySet()) {
                serializationStrategy.writeKey(file, entry.getKey());
                serializationStrategy.writeValue(file, entry.getValue());
            }
        } finally {
            Files.delete(lock.toPath());
            contents.clear();
            file.close();
        }
    }
}
