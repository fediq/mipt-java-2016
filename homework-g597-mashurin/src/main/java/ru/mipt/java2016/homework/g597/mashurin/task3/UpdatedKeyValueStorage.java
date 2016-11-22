package ru.mipt.java2016.homework.g597.mashurin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UpdatedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private String fileNameKey;
    private String fileMameValue;
    private Identification<K> keyIdentification;
    private Identification<V> valueIdentification;
    private RandomAccessFile keysStorage;
    private RandomAccessFile valuesStorage;
    private HashMap<K, Long> bufferShufts;
    private Boolean closedStreem;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public UpdatedKeyValueStorage(String nameDirectory, Identification<K> key, Identification<V> value)
            throws IOException {

        closedStreem = false;
        bufferShufts = new HashMap<K, Long>();
        File directory = new File(nameDirectory);
        keyIdentification = key;
        valueIdentification = value;
        if (!directory.isDirectory()) {
            throw new IOException("Isnt directory");
        }
        fileNameKey = nameDirectory + File.separator + "key.db";
        fileMameValue = nameDirectory + File.separator + "value.db";

        File keysFile = new File(fileNameKey);
        File valuesFile = new File(fileMameValue);

        if (keysFile.exists() && valuesFile.exists()) {
            keysStorage = new RandomAccessFile(keysFile, "rw");
            valuesStorage = new RandomAccessFile(valuesFile, "rw");
            read();
        } else {
            try {
                keysFile.createNewFile();
                valuesFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error");
            }
            keysStorage = new RandomAccessFile(keysFile, "rw");
            valuesStorage = new RandomAccessFile(valuesFile, "rw");
        }
    }

    private  void read() throws IOException {
        bufferShufts.clear();

        int readSize = IntegerIdentification.get().read(keysStorage);
        for (int i = 0; i < readSize; i++) {
            bufferShufts.put(keyIdentification.read(keysStorage), LongIdentification.get().read(keysStorage));
        }
    }

    private void write() throws IOException {
        keysStorage.seek(0);
        IntegerIdentification.get().write(keysStorage, bufferShufts.size());
        for (Map.Entry<K, Long> entry : bufferShufts.entrySet()) {
            keyIdentification.write(keysStorage, entry.getKey());
            LongIdentification.get().write(keysStorage, entry.getValue());
        }
        keysStorage.close();
        valuesStorage.close();
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            return bufferShufts.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            return bufferShufts.keySet().contains(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            write();
            closedStreem = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            return bufferShufts.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            bufferShufts.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            bufferShufts.put(key, valuesStorage.length());
            valuesStorage.seek(valuesStorage.length());
            valueIdentification.write(valuesStorage, value);
        } catch (IOException e) {
            throw new RuntimeException("Error write");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();
        try {
            if (closedStreem) {
                throw new IllegalStateException("Streem closed");
            }
            if (!exists(key)) {
                return null;
            }
            Long vallue = bufferShufts.get(key);
            valuesStorage.seek(vallue);
            return valueIdentification.read(valuesStorage);
        } catch (IOException e) {
            throw new RuntimeException("Error read");
        } finally {
            lock.readLock().unlock();
        }
    }
}