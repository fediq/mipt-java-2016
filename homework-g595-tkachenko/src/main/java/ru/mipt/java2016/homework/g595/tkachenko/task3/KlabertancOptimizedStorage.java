package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private final Map<K, V> readCache = new HashMap<>();
    private final Map<K, V> writeCache = new HashMap<>();
    private final Serialization<K> keySerialization;
    private final Serialization<V> valueSerialization;
    private final File keys;
    private final File values;
    private final RandomAccessFile valuesRandomAccess;
    private final String keysFileName;
    private final String valuesFileName;
    private final String directory;
    private boolean flagForClose;


    public KlabertancOptimizedStorage(String path, Serialization<K> k, Serialization<V> v) {

        keySerialization = k;
        valueSerialization = v;
        directory = path;
        keysFileName = directory + File.separator + "keys.db";
        valuesFileName = directory + File.separator + "values.db";

        keys = new File(keysFileName);
        values = new File(valuesFileName);

        if (keys.exists() ^ values.exists()) {
            throw new RuntimeException("Invalid storage architecture!");
        }

        if (keys.exists() && values.exists()) {
            try (DataInputStream dataInputStream = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(keys)))) {
                int keysCount = dataInputStream.readInt();
                for (int i = 0; i < keysCount; ++i) {
                    K key = keySerialization.read(dataInputStream);
                    offsets.put(key, dataInputStream.readLong());
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found!");
            } catch (IOException e) {
                throw new RuntimeException("Can't read from file!");
            }
            try {
                valuesRandomAccess = new RandomAccessFile(values, "rw");
            } catch (IOException e) {
                throw new RuntimeException("Can't create RA file!");
            }
        } else {
            try {
                keys.createNewFile();
                values.createNewFile();
                valuesRandomAccess = new RandomAccessFile(values, "rw");
            } catch (IOException e) {
                throw new RuntimeException("Can't create file for storage!");
            }
        }

        flagForClose = false;
    }

    private synchronized void putWriteCacheAsDatabaseOnDisk() {
        try {
            valuesRandomAccess.seek(valuesRandomAccess.length());
            for (Map.Entry<K, V> entry : writeCache.entrySet()) {
                offsets.remove(entry.getKey());
                offsets.put(entry.getKey(), valuesRandomAccess.length());
                valueSerialization.write(valuesRandomAccess, entry.getValue());
            }
            writeCache.clear();
        } catch (IOException e) {
            throw new RuntimeException("Can't access new file!");
        }
    }

    private synchronized void shutdownTheDatabase() {
        try (DataOutputStream dataOutputStream =
                     new DataOutputStream(new BufferedOutputStream(
                             new FileOutputStream(keysFileName)))) {
            dataOutputStream.writeInt(this.size());
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerialization.write(dataOutputStream, entry.getKey());
                dataOutputStream.writeLong(entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to storage!");
        } finally {
            try {
                valuesRandomAccess.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close values RAFile!");
            }
        }
    }

    private void isStorageClosed() {
        if (flagForClose) {
            throw new RuntimeException("Trying reach storage in closed state!");
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
            return writeCache.get(key);
        }

        Long position = offsets.get(key);

        try {
            valuesRandomAccess.seek(position);
            V value = valueSerialization.read(valuesRandomAccess);
            if (readCache.size() >= MAX_CACHE_SIZE) {
                readCache.clear();
            }
            readCache.put(key, value);
            return value;
        } catch (IOException exception) {
            throw new RuntimeException("Can't seek in file!");
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        isStorageClosed();
        if (readCache.containsKey(key) || writeCache.containsKey(key)) {
            return true;
        }
        return offsets.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        isStorageClosed();
        if (writeCache.size() >= MAX_CACHE_SIZE) {
            putWriteCacheAsDatabaseOnDisk();
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
        isStorageClosed();
        return offsets.size();
    }

    @Override
    public synchronized void close() {
        if (!flagForClose) {
            putWriteCacheAsDatabaseOnDisk();
            shutdownTheDatabase();
            flagForClose = true;
        }
    }
}