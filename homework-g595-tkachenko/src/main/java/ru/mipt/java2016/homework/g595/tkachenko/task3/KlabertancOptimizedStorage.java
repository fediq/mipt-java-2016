package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class KlabertancOptimizedStorage<K, V> implements KeyValueStorage<K, V> {

    private static final Integer MAX_CACHE_SIZE = 100;

    private final Map<K, Long> offsets = new HashMap<>();
    private Map<K, V> readCache = new HashMap<>();
    private Map<K, V> writeCache = new HashMap<>();
    private Serialization<K> keySerialization;
    private Serialization<V> valueSerialization;
    private RandomAccessFile keys;
    private RandomAccessFile values;
    private File lockAccess;
    private boolean flagForClose;
    private Integer closeCounter;

    public KlabertancOptimizedStorage(String path, Serialization<K> k, Serialization<V> v) {

        closeCounter = 0;
        keySerialization = k;
        valueSerialization = v;

        File dir = new File(path);
        if (!dir.isDirectory() || !dir.exists()) {
            throw new RuntimeException("Invalid path!");
        }

        File keysFile = new File(dir, "keys.db");
        File valuesFile = new File(dir, "values.db");

        try {
            keys = new RandomAccessFile(keysFile, "rws");
            values = new RandomAccessFile(valuesFile, "rws");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Files not found!");
        }

        if (keysFile.exists() && valuesFile.exists()) {
            try {
                if (keys.length() > 0 && values.length() > 0) {
                    getStorageFromDisk();
                }
            } catch (IOException e) {
                throw new RuntimeException("PLOHA OTKRIL FAILI");
            }
        }

        flagForClose = false;
    }

    private void getStorageFromDisk() {

        int size;

        try {
            keys.seek(0);
            size = keys.readInt();
        } catch (IOException e) {
            throw new RuntimeException("read size error");
        }
        for (int i = 0; i < size; ++i) {
            K key;
            Long offset = new Long(0);
            try {
                key = keySerialization.read(keys);
            } catch (IOException e) {
                throw new RuntimeException("key read error!");
            }
            try {
                offset = keys.readLong();
            } catch (IOException e) {
                throw new RuntimeException("offset read error error!");
            }
            offsets.put(key, offset);
        }

    }

    private void putStorageOnDisk() {

        pushCache();

        try {
            keys.close();
            values.close();
        } catch (IOException e) {
            throw new RuntimeException("close errors!");
        }

        offsets.clear();
        writeCache.clear();
        readCache.clear();

        flagForClose = true;
    }

    private void pushCache() {
        try {
            keys.seek(0);
            keys.writeInt(offsets.size());
            values.seek(values.length());
            keys.seek(keys.length());
            for (K key : writeCache.keySet()) {
                keySerialization.write(keys, key);

                keys.writeLong(values.length());
                if (offsets.containsKey(key)) {
                    if (offsets.get(key) == -1) {
                        offsets.remove(key);
                    }
                }
                offsets.put(key, values.length());

                valueSerialization.write(values, writeCache.get(key));
            }
        } catch (IOException e) {
            throw new RuntimeException("Cache pushing error!");
        }
    }

    private void isStorageClosed() {
        if (flagForClose) {
            throw new RuntimeException("Trying reach storage in closed state!");
        }
    }

    private void checkCache() throws IOException {
        if (writeCache.size() >= MAX_CACHE_SIZE) {
            pushCache();
            writeCache.clear();
        }
        if (readCache.size() >= MAX_CACHE_SIZE) {
            readCache.clear();
        }
    }

    @Override
    public synchronized V read(K key) {
        isStorageClosed();

        if (!offsets.containsKey(key)) {
            return null;
        }

        if (readCache.containsKey(key)) {
            return readCache.get(key);
        }

        if (writeCache.containsKey(key)) {
            readCache.put(key, writeCache.get(key));
            return writeCache.get(key);
        }



        Long offset = offsets.get(key);
        try {
            values.seek(offset);
            V value = valueSerialization.read(values);
            readCache.put(key, value);
            checkCache();
            return value;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        isStorageClosed();
        return offsets.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        isStorageClosed();

        try {
            checkCache();
        } catch (IOException e) {
            System.out.println("Error with cache checking!");
        }

        writeCache.put(key, value);
        offsets.put(key, new Long(-1));
    }

    @Override
    public synchronized void delete(K key) {
        isStorageClosed();
        if (offsets.containsKey(key)) {
            readCache.remove(key);
            writeCache.remove(key);
            offsets.remove(key);
        }
    }


    @Override
    public synchronized Iterator<K> readKeys() {
        isStorageClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        return offsets.size() + writeCache.size();
    }

    @Override
    public synchronized void close() throws IOException {
        closeCounter++;
        if (closeCounter == 1) {
            isStorageClosed();
            putStorageOnDisk();
        }
    }

}
