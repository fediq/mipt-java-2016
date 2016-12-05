package ru.mipt.java2016.homework.g597.kirilenko.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Natak on 27.10.2016.
 */



public class MyOptimisedStorage<K, V> implements KeyValueStorage<K, V> {
    private boolean close = false;
    private final RandomAccessFile keysRwFile;
    private RandomAccessFile valueRwFile;
    private final Map<K, Long> keysStorage = new HashMap<>();
    private final MySerialization<K> keySerialization;
    private final MySerialization<V> valueSerialization;
    private final File f;
    private final ReadWriteLock concurrentLock = new ReentrantReadWriteLock();
    private final String dirPath;

    private final Integer maxCacheSize = 256;
    private final Integer modificationLimit = 8192;
    private Integer currentModifications = 0;

    private final LoadingCache<K, V> smartCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize).build(
            new CacheLoader<K, V>() {
                @Override
                public V load(K k) throws Exception {
                    Long offset = keysStorage.get(k);
                    valueRwFile.seek(offset);
                    V value = valueSerialization.read(valueRwFile);
                    return value;
                }
            }
    );

    public MyOptimisedStorage(String path, MySerialization<K> serializeK,
                              MySerialization<V> serializeV) throws IOException {
        dirPath = path;
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("There is no such directory");
        }

        String check = path + File.separator + "checkProcesses";
        f = new File(check);
        if (!f.createNewFile()) {
            throw new IOException("Error");
        }

        close = false;
        String keysFullPath = path + File.separator + "keysStorage";
        String valueFullPath = path + File.separator + "valueStorage";
        keySerialization = serializeK;
        valueSerialization = serializeV;
        File kFile = new File(keysFullPath);
        File vFile = new File(valueFullPath);

        if (kFile.exists()) {
            keysRwFile = new RandomAccessFile(kFile, "rw");
            valueRwFile = new RandomAccessFile(vFile, "rw");
            int size = SerializationType.SerializationInteger.getSerialization().read(keysRwFile);
            for (int i = 0; i < size; ++i) {
                K key = keySerialization.read(keysRwFile);
                Long offset = SerializationType.SerializationLong.getSerialization().read(keysRwFile);
                keysStorage.put(key, offset);
            }
        } else {
            keysRwFile = new RandomAccessFile(kFile, "rw");
            valueRwFile = new RandomAccessFile(vFile, "rw");
        }
    }

    private void isClose() {
        if (close) {
            throw new IllegalStateException("Error");
        }
    }

    @Override
    public V read(K key) {
        isClose();
        if (!keysStorage.containsKey(key)) {
            return null;
        }
        concurrentLock.writeLock().lock();
        try {
            V value = smartCache.get(key);
            return value;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error");
        } finally {
            concurrentLock.writeLock().unlock();
        }
    }

    private void compress() throws IOException {
        concurrentLock.writeLock().lock();
        try {
            File newFile = new File(dirPath + File.separator + "temp");
            RandomAccessFile newStorage = new RandomAccessFile(newFile, "rw");
            newStorage.seek(0);

            int initialOffset = 0;
            valueRwFile.seek(initialOffset);
            while (true) {
                Long offset = valueRwFile.getFilePointer();
                if (offset >= valueRwFile.length()) {
                    break;
                }
                V value = valueSerialization.read(valueRwFile);
                K key = keySerialization.read(valueRwFile);
                if (!keysStorage.containsKey(key)) {
                    continue;
                }
                keysStorage.put(key, newStorage.getFilePointer());
                valueSerialization.write(newStorage, value);
                keySerialization.write(newStorage, key);
            }
            valueRwFile.close();
            newFile.renameTo(new File(dirPath + File.separator + "valueStorage"));
            newStorage.close();
            valueRwFile = new RandomAccessFile(dirPath + File.separator + "valueStorage", "rw");
        } catch (IOException e) {
            throw e;
        } finally {
            concurrentLock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        isClose();
        return keysStorage.containsKey(key);
    }

    private void updateModifications() {
        currentModifications++;
        if (currentModifications >= modificationLimit) {
            try {
                compress();
                currentModifications = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write(K key, V value) {
        isClose();
        concurrentLock.writeLock().lock();
        try {
            Long offset;
            offset = valueRwFile.length();
            valueRwFile.seek(offset);
            valueSerialization.write(valueRwFile, value);
            keySerialization.write(valueRwFile, key);
            keysStorage.put(key, offset);
        } catch (IOException e) {
            throw new RuntimeException("Error");
        } finally {
            concurrentLock.writeLock().unlock();
            updateModifications();
        }
    }

    @Override
    public void delete(Object key) {
        isClose();
        if (!keysStorage.containsKey(key)) {
            return;
        }
        keysStorage.remove(key);
        updateModifications();
    }

    @Override
    public Iterator readKeys() {
        isClose();
        return keysStorage.keySet().iterator();
    }

    @Override
    public int size() {
        isClose();
        return keysStorage.size();
    }

    @Override
    public void close() throws IOException {
        if (close) {
            return;
        }
        close = true;
        try {
            f.delete();
            keysRwFile.seek(0);
            keysRwFile.setLength(0);
            SerializationType.SerializationInteger.getSerialization().write(keysRwFile, keysStorage.size());
            Set<K> keys = keysStorage.keySet();
            for (K k : keys) {
                keySerialization.write(keysRwFile, k);
                SerializationType.SerializationLong.getSerialization().write(keysRwFile, keysStorage.get(k));
            }
        } finally {
            keysRwFile.close();
            valueRwFile.close();
        }
    }
}
