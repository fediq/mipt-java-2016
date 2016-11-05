package ru.mipt.java2016.homework.g595.murzin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<K, V> implements KeyValueStorage<K, V> {

    public static final String KEYS_FILE_NAME = "keys.dat";

    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

    private FileLock lock;
    private File storageDirectory;
    private LSMBackgroundThread backgroundThread = new LSMBackgroundThread();

    private static class Offset {
        public static SerializationStrategy<Offset> STRATEGY = new SerializationStrategy<Offset>() {
            @Override
            public void serializeToStream(Offset offset, DataOutputStream output) throws IOException {

            }

            @Override
            public Offset deserializeFromStream(DataInputStream input) throws IOException {
                return null;
            }
        };
    }

    private Map<K, Offset> keys = new HashMap<>();
    private Map<K, V> cache = new HashMap<>();
    private int numberTablesOnDisk;

    public LSMStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        storageDirectory = new File(path);
        if (!storageDirectory.isDirectory() || !storageDirectory.exists()) {
            throw new RuntimeException("Path " + path + " is not a valid directory name");
        }

        synchronized (LSMStorage.class) {
            try {
                // It is between processes lock!!!
                lock = new RandomAccessFile(new File(path, ".lock"), "rw").getChannel().lock();
            } catch (IOException e) {
                throw new RuntimeException("Can't create lock file", e);
            }
        }

        readAllKeys();
        numberTablesOnDisk = new File(storageDirectory, "storage0.db").exists() ? 1 : 0;
    }

    private void readAllKeys() {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        try (DataInputStream input = new DataInputStream(new FileInputStream(keysFile))) {
            int n = input.readInt();
            for (int i = 0; i < n; i++) {
                K key = keySerializationStrategy.deserializeFromStream(input);
                Offset offset = Offset.STRATEGY.deserializeFromStream(input);
                keys.put(key, offset);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void checkForCacheSize() {
        if (cache.size() <= 100) {
            return;
        }

        File newStorageFile = new File(storageDirectory, "storage" + numberTablesOnDisk++ + ".dat");
        backgroundThread.submit(() -> pushCacheToDisk(cache, newStorageFile, keySerializationStrategy, valueSerializationStrategy));

        cache = new HashMap<>();
    }

    private static <K, V> void pushCacheToDisk(Map<K, V> cache, File newStorageFile, SerializationStrategy<K> keySerializationStrategy, SerializationStrategy<V> valueSerializationStrategy) {
        try {
            boolean exists = newStorageFile.createNewFile();
            if (exists) {
                throw new RuntimeException("File " + newStorageFile.getAbsolutePath() + " already exists");
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't create one of storage file " + newStorageFile.getAbsolutePath(), e);
        }

        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(newStorageFile))) {
            output.writeInt(cache.size());
            for (Map.Entry<K, V> entry : cache.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                valueSerializationStrategy.serializeToStream(entry.getValue(), output);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to one of storage files " + newStorageFile.getAbsolutePath(), e);
        }
    }

    @Override
    public synchronized V read(K key) {
        if (!keys.containsKey(key)) {
            return null;
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        throw new RuntimeException();
    }

    @Override
    public synchronized boolean exists(K key) {
        return keys.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        cache.put(key, value);
        checkForCacheSize();
    }

    @Override
    public synchronized void delete(K key) {
        keys.remove(key);
        cache.remove(key);
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        return null;
    }

    @Override
    public synchronized int size() {
        return keys.size();
    }

    @Override
    public synchronized void close() throws IOException {
//        writeAllKeys();
        backgroundThread.shutdown();
        lock.release();
    }
}
