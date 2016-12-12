package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by macbook on 30.10.16.
 */

public class MyDisabledKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map;
    private RandomAccessFile raFile; // raFile - это типа random access file (ну а вдруг )))))0)
    private File lock;
    private final SerializationStrategy<K> keyStrategy;
    private final SerializationStrategy<V> valueStrategy;
    private String mode = "rw"; // По умолчанию выставили чтение/запись
    private boolean isFileOpened;

    MyDisabledKeyValueStorage(String path, SerializationStrategy<K> key, SerializationStrategy<V> value)
            throws IOException {
        if (path == null) {
            throw new NullPointerException("The pathname argument is null");
        }
        lock = new File(path + File.separator + "zarabotayples");
        if (!lock.createNewFile()) {
            throw new IllegalStateException("Can not work with one file from multiple storages");
        }
        isFileOpened = true;
        map = new HashMap<>();
        keyStrategy = key;
        valueStrategy = value;
        String pathname = path + File.separator + "storage.txt";
        try {
            File file = new File(pathname);
            if (file.createNewFile()) {
                raFile = new RandomAccessFile(file, mode);
            } else {
                raFile = new RandomAccessFile(file, mode);
                raFile.seek(0);
                int numberOfElements = raFile.readInt();
                for (int i = 0; i < numberOfElements; i++) {
                    K currentKey = this.keyStrategy.read(raFile);
                    V currentValue = this.valueStrategy.read(raFile);
                    map.put(currentKey, currentValue);
                }
            }
        } catch (FileNotFoundException e) {
            lock.delete();
            throw new FileNotFoundException("The given string does not denote an existing file");
        }
    }

    private void checkFileNotClosed() throws IllegalStateException {
        if (!isFileOpened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    @Override
    public V read(K key) {
        checkFileNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkFileNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkFileNotClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkFileNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkFileNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkFileNotClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        try {
            if (isFileOpened) {
                lock.delete();
                raFile.setLength(0);
                raFile.seek(0); // на всякий случай
                raFile.writeInt(map.size());
                for (HashMap.Entry<K, V> entry : map.entrySet()) {
                    keyStrategy.write(raFile, entry.getKey());
                    valueStrategy.write(raFile, entry.getValue());
                }
            }
            isFileOpened = false;

        } catch (IOException e) {
            throw new IOException("Couldn't write during the close of storage");
        }
    }
}
