package ru.mipt.java2016.homework.g595.ferenets.task3;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ferenets.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyOptimizedStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int MAX_MEM_SIZE = 100;
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;
    private HashMap<K, V> map = new HashMap<K, V>();
    private HashMap<K, Long> keyValueOffset = new HashMap<K, Long>();
    private boolean opened;
    private RandomAccessFile storage;
    private RandomAccessFile offsets;
    private String storagePath;
    private String offsetsPath;
    private FileLock lockFile;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    MyOptimizedStorage(String path, SerializationStrategy<K> argKeySerialization,
                       SerializationStrategy<V> argValueSerialization) throws IOException {
        try {
            keySerialization = argKeySerialization;
            valueSerialization = argValueSerialization;
            storagePath = path + File.separator + "storage.txt";
            offsetsPath = path + File.separator + "offsets.txt";
            File offsetsFile = new File(offsetsPath);
            File storageFile = new File(storagePath);
            if(!offsetsFile.createNewFile()) {
                offsets = new RandomAccessFile(offsetsPath, "rw");
                storage = new RandomAccessFile(storagePath, "rw");
                lockFile = offsets.getChannel().lock();
                int offsetsSize = offsets.readInt();
                for (int i = 0; i < offsetsSize; i++) {
                    K key = keySerialization.read(offsets);
                    long offset = offsets.readLong();
                    keyValueOffset.put(key, offset);
                }
                lockFile.release();
                offsets.close();
            } else {
                offsets = new RandomAccessFile(offsetsPath, "rw");
                storage = new RandomAccessFile(storagePath, "rw");
                lockFile = offsets.getChannel().lock();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkFileAccess() {
        if (!opened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    private void pushMapIntoFile() throws IOException {
        readWriteLock.writeLock().lock();
        if (map.size() < MAX_MEM_SIZE) {
            return;
        }
        storage.seek(storage.length());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (!keyValueOffset.containsKey(entry.getKey())) {
                keyValueOffset.put(entry.getKey(), storage.getFilePointer());
                valueSerialization.write(storage, entry.getValue());
            }
        }
        map.clear();
        readWriteLock.writeLock().unlock();
    }

    @Override
    public V read(K key) {
        readWriteLock.writeLock().lock();
        checkFileAccess();
        try {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            if (keyValueOffset.containsKey(key)) {
                long offset = keyValueOffset.get(key);
                storage.seek(offset);
                V value = valueSerialization.read(storage);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public boolean exists(K key) {
        checkFileAccess();
        return keyValueOffset.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        readWriteLock.writeLock().lock();
        checkFileAccess();
        map.put(key, value);
        try {
            pushMapIntoFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        readWriteLock.readLock().lock();
        checkFileAccess();
        if (exists(key)) {
            map.remove(key);
            keyValueOffset.remove(key);
        }
        readWriteLock.readLock().unlock();
    }

    @Override
    public Iterator<K> readKeys() {
        readWriteLock.readLock().lock();
        try{
            checkFileAccess();
            return map.keySet().iterator();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        readWriteLock.readLock().lock();
        try{
                checkFileAccess();
                return map.keySet().size() + keyValueOffset.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        readWriteLock.writeLock().lock();
        opened = false;
        try {
            pushMapIntoFile();
            offsets = new RandomAccessFile(offsetsPath, "rw");
            lockFile = offsets.getChannel().lock();
            offsets.writeInt(keyValueOffset.size());
            for (Map.Entry<K, Long> entry : keyValueOffset.entrySet()) {
                keySerialization.write(offsets, entry.getKey());
                offsets.writeLong(entry.getValue());
            }
            lockFile.release();
            storage.close();
            offsets.close();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
