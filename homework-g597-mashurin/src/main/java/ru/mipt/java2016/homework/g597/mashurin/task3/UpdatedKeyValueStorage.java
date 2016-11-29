package ru.mipt.java2016.homework.g597.mashurin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UpdatedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final Identification<K> keyIdentification;
    private final Identification<V> valueIdentification;
    private final RandomAccessFile keysStorage;
    private RandomAccessFile valuesStorage;
    private Map<K, Long> bufferOffsets;
    private Boolean closed = false;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private long changesCounter = 0;
    private final String nameDirectory;

    public UpdatedKeyValueStorage(String name, Identification<K> key, Identification<V> value)
            throws IOException {

        nameDirectory = name;
        bufferOffsets = new HashMap<K, Long>();
        File directory = new File(nameDirectory);
        keyIdentification = key;
        valueIdentification = value;
        if (!directory.isDirectory()) {
            throw new IOException("Isn't directory");
        }

        File keysFile = new File(nameDirectory + File.separator + "key.db");
        File valuesFile = new File(nameDirectory + File.separator + "value.db");

        if (keysFile.exists() && valuesFile.exists()) {
            keysStorage = new RandomAccessFile(keysFile, "rw");
            valuesStorage = new RandomAccessFile(valuesFile, "rw");
            load();
        } else {
            keysFile.createNewFile();
            valuesFile.createNewFile();
            keysStorage = new RandomAccessFile(keysFile, "rw");
            valuesStorage = new RandomAccessFile(valuesFile, "rw");
        }
    }

    private void load() throws IOException {
        bufferOffsets.clear();

        changesCounter = LongIdentification.get().read(keysStorage);
        int readSize = IntegerIdentification.get().read(keysStorage);
        for (int i = 0; i < readSize; i++) {
            bufferOffsets.put(keyIdentification.read(keysStorage), LongIdentification.get().read(keysStorage));
        }
    }

    private void save() throws IOException {
        try {
            keysStorage.seek(0);
            keysStorage.setLength(0);
            LongIdentification.get().write(keysStorage, changesCounter);
            IntegerIdentification.get().write(keysStorage, bufferOffsets.size());
            for (Map.Entry<K, Long> entry : bufferOffsets.entrySet()) {
                keyIdentification.write(keysStorage, entry.getKey());
                LongIdentification.get().write(keysStorage, entry.getValue());
            }
        } finally {
            keysStorage.close();
            valuesStorage.close();
        }
    }

    private void update() throws IOException {
        changesCounter = 0;

        Map<K, Long> newBufferOffsets = new HashMap<K, Long>();
        Map<V, Long> newBuffer = new HashMap<V, Long>();

        keysStorage.seek(0);
        keysStorage.setLength(0);
        LongIdentification.get().write(keysStorage, changesCounter);
        IntegerIdentification.get().write(keysStorage, bufferOffsets.size());

        File valuesFile = new File(nameDirectory + File.separator + "value.db");
        File newValuesFile = new File(nameDirectory + File.separator + "newValue.db");

        newValuesFile.createNewFile();

        RandomAccessFile newFileForValues = new RandomAccessFile(newValuesFile, "rw");

        newFileForValues.seek(0);
        newFileForValues.setLength(0);
        for (Map.Entry<K, Long> entry : bufferOffsets.entrySet()) {
            valuesStorage.seek(entry.getValue());
            V value = valueIdentification.read(valuesStorage);
            if (!newBuffer.containsKey(value)) {
                newBuffer.put(value, newFileForValues.length());
                valueIdentification.write(newFileForValues, value);
            }
            newBufferOffsets.put(entry.getKey(), newBuffer.get(value));
            keyIdentification.write(keysStorage, entry.getKey());
            LongIdentification.get().write(keysStorage, newBuffer.get(value));
        }
        bufferOffsets = newBufferOffsets;
        valuesFile.delete();
        newValuesFile.renameTo(valuesFile);
        valuesStorage = newFileForValues;
    }

    private void checkUpdate() {
        if (changesCounter >= bufferOffsets.size()) {
            try {
                update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkClosing() {
        if (closed) {
            throw new IllegalStateException("Stream closed");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            checkClosing();
            return bufferOffsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            checkClosing();
            return bufferOffsets.keySet().contains(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            lock.writeLock().lock();
            try {
                save();
                closed = true;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkClosing();
            return bufferOffsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            checkClosing();
            bufferOffsets.remove(key);
            changesCounter += 1;
            checkUpdate();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkClosing();
            bufferOffsets.put(key, valuesStorage.length());
            valuesStorage.seek(valuesStorage.length());
            valueIdentification.write(valuesStorage, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();
        try {
            checkClosing();
            if (!bufferOffsets.containsKey(key)) {
                return null;
            }
            Long value = bufferOffsets.get(key);
            valuesStorage.seek(value);
            return valueIdentification.read(valuesStorage);
        } catch (IOException e) {
            throw new RuntimeException("Error read");
        } finally {
            lock.readLock().unlock();
        }
    }
}