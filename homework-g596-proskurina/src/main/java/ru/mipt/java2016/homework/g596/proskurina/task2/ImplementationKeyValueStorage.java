package ru.mipt.java2016.homework.g596.proskurina.task2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class ImplementationKeyValueStorage<K, V> implements ru.mipt.java2016.homework.base.task2.KeyValueStorage<K, V> {

    private final Map<K, Long> keyPositionMap;

    private final SerialiserInterface<K> keySerialiser;
    private final SerialiserInterface<V> valueSerialiser;

    private final Set<K> deleteKeySet = new HashSet<>();

    private final FileWorker keyPositionFile;
    private final FileWorker valuesFile;
    private final FileWorker deleteKeyFile;
    private final FileWorker lockFile;

    private final String directoryPath;

    private Long currentPositionInValuesFile = new Long(0);
    private final Integer lock = 42;

    private boolean writing = true;
    private boolean needRebuild = false;
    private boolean openFlag = true;

    private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
            .maximumSize(42)
            .build(
                    new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws RuntimeException {
                            V result = readKey(keyPositionMap.get(k));
                            if (result == null) {
                                throw new RuntimeException("no such key");
                            }
                            return result;
                        }
                    });

    public ImplementationKeyValueStorage(SerialiserInterface<K> keySerialiser, SerialiserInterface<V> valueSerialiser,
                                         String directoryPath) {

        this.keySerialiser = keySerialiser;
        this.valueSerialiser = valueSerialiser;

        keyPositionMap = new HashMap<>();

        if (directoryPath == null || directoryPath.equals("")) {
            this.directoryPath = "";
        } else {
            this.directoryPath = directoryPath + File.separator;
        }
        keyPositionFile = new FileWorker(this.directoryPath + "keyPositionFile.db");
        valuesFile = new FileWorker(this.directoryPath + "valuesFile.db");
        deleteKeyFile = new FileWorker(this.directoryPath + "deleteKeyFile.db");
        lockFile = new FileWorker(this.directoryPath + "lockFile.db");
        if (lockFile.exist()) {
            throw new RuntimeException("we already have working storage");
        } else {
            lockFile.createFile();
        }
        if (!keyPositionFile.exist()) {
            keyPositionFile.createFile();
            valuesFile.createFile();
            deleteKeyFile.createFile();
        } else {
            currentPositionInValuesFile =  valuesFile.fileLength();
            initStorage();
        }
        valuesFile.appendMode();
    }

    private void initStorage() {
        keyPositionFile.close();
        int cnt = 0;
        String nextKey = keyPositionFile.read();
        while (nextKey != null) {
            Long position = Long.parseLong(keyPositionFile.read());
            keyPositionMap.put(keySerialiser.deserialise(nextKey), position);
            nextKey = keyPositionFile.read();
            cnt++;
        }
        nextKey = deleteKeyFile.read();
        while (nextKey != null) {
            K key = keySerialiser.deserialise(nextKey);
            deleteKeySet.add(key);
            keyPositionMap.remove(key);
            nextKey = deleteKeyFile.read();
            cnt++;
        }
        keyPositionFile.appendMode();
        deleteKeyFile.close();
        if (cnt > 2 * keyPositionMap.size()) {
            needRebuild = true;
        }
    }

    private void checkIfStorageIsOpen() {
        if (!openFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        synchronized (lock) {
            checkIfStorageIsOpen();
            Long position = keyPositionMap.get(key);
            if (position != null) {
                try {
                    return cacheValues.get(key);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (lock) {
            checkIfStorageIsOpen();
            return keyPositionMap.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (lock) {
            checkIfStorageIsOpen();
            deleteKeySet.remove(key);
            keyPositionMap.put(key, currentPositionInValuesFile);
            writeToFile(key, value);
        }
    }

    @Override
    public void delete(K key) {
        synchronized (lock) {
            checkIfStorageIsOpen();
            deleteKeySet.add(key);
            keyPositionMap.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (lock) {
            checkIfStorageIsOpen();
            return keyPositionMap.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            checkIfStorageIsOpen();
            return keyPositionMap.size();
        }
    }

    @Override
    public void close()  throws IOException {
        synchronized (lock) {
            if (openFlag) {
                openFlag = false;
                if (needRebuild) {
                    rebuild();
                } else {
                    writeToFileDeleteKeySet();
                    deleteKeyFile.close();
                    keyPositionFile.close();
                    valuesFile.close();
                }
                lockFile.delete();
            }
        }
    }

    private void rebuild() {
        FileWorker newValuesFile = new FileWorker(directoryPath + "newValuesFile.db");
        newValuesFile.createFile();
        deleteKeyFile.close();
        keyPositionFile.close();
        currentPositionInValuesFile = 0L;
        for (Map.Entry<K, Long> entry: keyPositionMap.entrySet()) {
            keyPositionFile.write(keySerialiser.serialise(entry.getKey()));
            keyPositionFile.write(currentPositionInValuesFile.toString());
            currentPositionInValuesFile += newValuesFile.write(valueSerialiser.serialise(readKey(entry.getValue())));
        }
        keyPositionFile.flushSubmit();
        newValuesFile.flushSubmit();
        valuesFile.close();
        valuesFile.delete();
        newValuesFile.rename(directoryPath + "valuesFile.db");
        newValuesFile.close();
    }

    private V readKey(long position) {
        if (writing) {
            valuesFile.close();
            writing = false;
        }
        valuesFile.goToPosition(position);
        return valueSerialiser.deserialise(valuesFile.read());
    }

    private void writeToFile(K key, V value) {
        if (!writing) {
            valuesFile.close();
            valuesFile.appendMode();
            writing = true;
        }
        keyPositionFile.write(keySerialiser.serialise(key));
        keyPositionFile.write(currentPositionInValuesFile.toString());
        currentPositionInValuesFile +=  valuesFile.write(valueSerialiser.serialise(value));
    }

    private void writeToFileDeleteKeySet() {
        if (!deleteKeyFile.exist()) {
            deleteKeyFile.createFile();
        }
        for (K entry : deleteKeySet) {
            deleteKeyFile.write(keySerialiser.serialise(entry));
        }
        deleteKeyFile.flushSubmit();
        deleteKeySet.clear();
    }
}
