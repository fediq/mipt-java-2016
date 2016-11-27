package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class KlabertancOptimizedStorage<K, V> implements KeyValueStorage<K, V> {

    private static final Integer MAX_CACHE_SIZE = 100;

    private final Map<K, KeyPosition> offsets = new HashMap<>();
    private final Map<K, V> readCache = new HashMap<>();
    private final Map<K, V> writeCache = new HashMap<>();
    private final ArrayList<RandomAccessFile> files = new ArrayList<>();
    private final Serialization<K> keySerialization;
    private final Serialization<V> valueSerialization;
    private final String storageFileName;
    private static String directory;
    private final File storage;
    private boolean flagForClose;
    private Integer closeCounter;


    public KlabertancOptimizedStorage(String path, Serialization<K> k, Serialization<V> v) {

        closeCounter = 0;
        keySerialization = k;
        valueSerialization = v;
        directory = path;
        storageFileName = directory + File.separator + "storage.db";
        storage = new File(storageFileName);

        if (storage.exists()) {
            getStorageFromDisk();
        } else {
            try {
                storage.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Can't create file for storage!");
            }
        }

        flagForClose = false;
    }

    private void getStorageFromDisk() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(storage));
            int filesCount = dataInputStream.readInt();
            for (int i = 0; i < filesCount; ++i) {
                File newFile = new File(directory + File.separator + Integer.toString(i) + ".pdb");
                if (!newFile.exists()) {
                    throw new RuntimeException("Part of the database is missing!");
                }
                files.add(new RandomAccessFile(newFile, "rws"));
            }
            int keysCount = dataInputStream.readInt();
            for (int i = 0; i < keysCount; ++i) {
                K key = keySerialization.read(dataInputStream);
                offsets.put(key, new KeyPosition(dataInputStream.readLong(),
                        dataInputStream.readLong()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found!");
        } catch (IOException e) {
            throw new RuntimeException("Can't read from file!");
        }
    }

    private synchronized void putWriteCacheAsDatabaseOnDisk() {
        String newFileName = directory + File.separator + Integer.toString(files.size()) + ".pdb";
        File newFile = new File(newFileName);
        if (newFile.exists()) {
            throw new RuntimeException("Unexpected file!");
        }
        try {
            newFile.createNewFile();
            RandomAccessFile newRandomAccessFile = new RandomAccessFile(newFile, "rws");
            newRandomAccessFile.setLength(0);
            newRandomAccessFile.seek(0);
            for (Map.Entry<K, V> entry : writeCache.entrySet()) {
                KeyPosition keyPosition = new KeyPosition(files.size(),
                        newRandomAccessFile.getFilePointer());
                offsets.remove(entry.getKey());
                offsets.put(entry.getKey(), keyPosition);
                valueSerialization.write(newRandomAccessFile, entry.getValue());
            }
            writeCache.clear();
            files.add(newRandomAccessFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't access new file!");
        }
    }

    private synchronized void shutdownTheDatabase() {
        try {
            DataOutputStream dataOutputStream =
                    new DataOutputStream(new FileOutputStream(storageFileName));
            dataOutputStream.writeInt(files.size());
            dataOutputStream.writeInt(this.size());
            for (Map.Entry<K, KeyPosition> entry : offsets.entrySet()) {
                keySerialization.write(dataOutputStream, entry.getKey());
                dataOutputStream.writeLong(entry.getValue().getFileNumber());
                dataOutputStream.writeLong(entry.getValue().getPositionInFile());
            }
            dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't write to storage!");
        }
    }

    private void isStorageClosed() {
        if (flagForClose) {
            throw new RuntimeException("Trying reach storage in closed state!");
        }
    }

    private void checkCache(Map<K, V> map) {
        if (map.size() >= MAX_CACHE_SIZE) {
            map.clear();
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

        KeyPosition keyPosition = offsets.get(key);
        RandomAccessFile dbPartFile = files.get((int) keyPosition.getFileNumber());
        try {
            dbPartFile.seek(keyPosition.getPositionInFile());
            V value = valueSerialization.read(dbPartFile);
            checkCache(readCache);
            readCache.put(key, value);
            return value;
        } catch (IOException exception) {
            throw new RuntimeException("Can't seek in file!");
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
        if (writeCache.size() >= MAX_CACHE_SIZE) {
            putWriteCacheAsDatabaseOnDisk();
        }
        writeCache.put(key, value);
        offsets.put(key, new KeyPosition(-1, -1));
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
    public synchronized void close() throws IOException {
        closeCounter++;
        if (closeCounter == 1) {
            isStorageClosed();
            putWriteCacheAsDatabaseOnDisk();
            shutdownTheDatabase();
            for (int i = 0; i < files.size(); ++i) {
                files.get(i).close();
            }
            flagForClose = true;
        }
    }
}
