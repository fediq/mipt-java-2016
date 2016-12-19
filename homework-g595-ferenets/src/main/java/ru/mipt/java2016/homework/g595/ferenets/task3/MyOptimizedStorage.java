package ru.mipt.java2016.homework.g595.ferenets.task3;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ferenets.task2.SerializationStrategy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MyOptimizedStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int MAX_MEM_SIZE = 100;
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;
    private HashMap<K, V> map = new HashMap<K, V>();
    private TreeMap<K, Long> keyValueOffset = new TreeMap<K, Long>();
    private HashSet<K> keySet = new HashSet<K>();
    private boolean opened;
    private RandomAccessFile storage;
    private RandomAccessFile offsets;
    private File storageFile;
    private File offsetsFile;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    MyOptimizedStorage(String path, SerializationStrategy<K> argKeySerialization,
                       SerializationStrategy<V> argValueSerialization) throws IOException {
        try {
            keySerialization = argKeySerialization;
            valueSerialization = argValueSerialization;
            String storagePath = path + File.separator + "storage.txt";
            String offsetsPath = path + File.separator + "offsets.txt";
            opened = true;
            offsetsFile = new File(offsetsPath);
            storageFile = new File(storagePath);
            if (!offsetsFile.createNewFile()) {
                offsets = new RandomAccessFile(offsetsFile, "rw");
                storage = new RandomAccessFile(storageFile, "rw");
                int offsetsSize = offsets.readInt();
                for (int i = 0; i < offsetsSize; i++) {
                    K key = keySerialization.read(offsets);
                    long offset = offsets.readLong();
                    keyValueOffset.put(key, offset);
                    keySet.add(key);
                }
            } else {
                offsets = new RandomAccessFile(offsetsFile, "rw");
                storage = new RandomAccessFile(storageFile, "rw");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        offsets.close();
    }


    private void checkFileAccess() {
        if (!opened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    private void pushMapIntoFile() throws IOException {
        readWriteLock.writeLock().lock();
        try {
            storage.seek(storage.length());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                keyValueOffset.put(entry.getKey(), storage.getFilePointer());
                valueSerialization.write(storage, entry.getValue());
            }
            map.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
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
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("Key is wrong");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        readWriteLock.readLock().lock();
        try {
            checkFileAccess();
            return keySet.contains(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        readWriteLock.writeLock().lock();
        checkFileAccess();
        map.put(key, value);
        keySet.add(key);
        try {
            if (map.size() > MAX_MEM_SIZE) {
                pushMapIntoFile();
            }
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
            map.put(key, null);
            keyValueOffset.remove(key);
            keySet.remove(key);
        }
        readWriteLock.readLock().unlock();
    }

    @Override
    public Iterator<K> readKeys() {
        readWriteLock.readLock().lock();
        try {
            checkFileAccess();
            return keySet.iterator();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        readWriteLock.readLock().lock();
        try {
            checkFileAccess();
            return keySet.size();
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
            offsets = new RandomAccessFile(offsetsFile, "rw");
            offsets.writeInt(keyValueOffset.size());
            for (Map.Entry<K, Long> entry : keyValueOffset.entrySet()) {
                keySerialization.write(offsets, entry.getKey());
                offsets.writeLong(entry.getValue());
            }
            storage.close();
            offsets.close();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
