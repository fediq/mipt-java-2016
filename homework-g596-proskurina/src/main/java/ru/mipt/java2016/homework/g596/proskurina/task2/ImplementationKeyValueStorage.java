package ru.mipt.java2016.homework.g596.proskurina.task2;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class ImplementationKeyValueStorage<K, V> implements ru.mipt.java2016.homework.base.task2.KeyValueStorage<K, V> {

    private final Map<K, Long> keyPositionMap = new HashMap<>();

    private final SerialiserInterface<K> keySerialiser;
    private final SerialiserInterface<V> valueSerialiser;

    private final Set<K> deleteKeySet = new HashSet<>();

    private final FileWorker keyPositionFile;
    private final FileWorker valuesFile;
    private final FileWorker deleteKeyFile;
    private final FileWorker lockFile;
    private final FileWorker validationFile;

    private final String dirPath;

    private Long currentPositionInValuesFile = new Long(0);
    private final Integer lock = 42;

    private boolean writing = true;
    private boolean needRebuild = false;
    private static final String VALIDATION_STRING = "EtotFileZapisanMoeiProgoi";
    private boolean openFlag = true;

    private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws StorageException {
                            V result = loadKey(keyPositionMap.get(k));
                            if (result == null) {
                                throw new StorageException("no such key");
                            }
                            return result;
                        }
                    });

    public ImplementationKeyValueStorage(//String keyName, String valueName,
                                         SerialiserInterface<K> keySerialiser, SerialiserInterface<V> valueSerialiser,
                                         String directoryPath) {

        this.keySerialiser = keySerialiser;
        this.valueSerialiser = valueSerialiser;

        keyPositionMap = Collections.synchronizedMap(new HashMap<>());

        if (dirPath == null || dirPath.equals("")) {
            this.dirPath = "";
        } else {
            this.dirPath = dirPath + File.separator;
        }
        keyPositionFile = new FileWorker(this.dirPath + "keyPositionFile.db");
        valuesFile = new FileWorker(this.dirPath + "valuesFile.db");
        deleteKeyFile = new FileWorker(this.dirPath + "deleteKeyFile.db");
        validationFile = new FileWorker(this.dirPath + "validationFile.db");
        lockFile = new FileWorker(this.dirPath + "lockFile.db");
        if (lockFile.exist()) {
            throw new RuntimeException("there is working storage");
        } else {
            lockFile.createFile();
        }
        if (!keyPositionFile.exist()) {
            keyPositionFile.createFile();
            valuesFile.createFile();
            deleteKeyFile.createFile();
            validationFile.createFile();
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
            try {
                keyPositionMap.put(keySerialiser.deserialise(nextKey), position);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = keyPositionFile.read();
            cnt++;
        }
        nextKey = deleteKeyFile.read();
        while (nextKey != null) {
            try {
                K key = keySerialiser.deserialise(nextKey);
                deleteKeySet.add(key);
                keyPositionMap.remove(key);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = deleteKeyFile.read();
            cnt++;
        }
        keyPositionFile.appendMode();
        deleteKeyFile.close();
        if (cnt > 2 * keyPositionMap.size()) {
            needRebuild = true;
        }
    }

    private void checkIfFileIsOpen() {
        if (!openFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        synchronized (lock) {
            checkIfFileIsOpen();
            Long offset = keyPositionMap.get(key);
            if (offset != null) {
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
            checkIfFileIsOpen();
            return keyPositionMap.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (lock) {
            checkIfFileIsOpen();
            deleteKeySet.remove(key);
            keyPositionMap.put(key, currentPositionInValuesFile);
            flush(key, value);
        }
    }

    @Override
    public void delete(K key) {
        synchronized (lock) {
            checkIfFileIsOpen();
            deleteKeySet.add(key);
            keyPositionMap.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (lock) {
            checkIfFileIsOpen();
            return keyPositionMap.keySet().iterator();
        }
    }
    @Override
    public int size() {
        synchronized (lock) {
            checkIfFileIsOpen();
            return keyPositionMap.size();
        }
    }

    @Override
    public void close()  throws IOException {
        synchronized (lock) {
            if(openFlag) {
                openFlag = false;
                if (needRebuild) {
                    rebuild();
                } else {
                    flushDeletes();
                    deleteKeyFile.close();
                    keyPositionFile.close();
                     valuesFile.close();
                    writeChecksum();
                }
                lockFile.delete();
            }
        }
    }

    private void rebuild() {
        FileWorker newVal = new FileWorker(dirPath + "nva.db", false);
        newVal.createFile();
        deleteKeyFile.close();
        deleteKeyFile.delete();
        deleteKeyFile.createFile();
        validationFile.close();
        validationFile.delete();
        validationFile.createFile();
        keyPositionFile.close();
        keyPositionFile.delete();
        keyPositionFile.createFile();
        currentPositionInValuesFile = 0L;
        for (Map.Entry<K, Long> entry: keyPositionMap.entrySet()) {
            keyPositionFile.write(keySerialiser.serialise(entry.getKey()));
            keyPositionFile.write(currentPositionInValuesFile.toString());
            currentPositionInValuesFile += newVal.write(valueSerialiser.serialise(loadKey(entry.getValue())));
        }
        keyPositionFile.writeSubmit();
        newVal.writeSubmit();
        writeChecksum();
         valuesFile.close();
         valuesFile.delete();
        newVal.rename(dirPath + "valuesFile.db");
        newVal.close();
    }

    private void writeChecksum() {
        validationFile.close();
        validationFile.write(Long.toString(keyPositionFile.getCheckSum()));
        validationFile.writeSubmit();
    }
    private V loadKey(long position) {
        if (writing) {
             valuesFile.close();
            writing = false;
        }
         valuesFile.goToPosition(position);
        try {
            return valueSerialiser.deserialise( valuesFile.read());
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    private void flush(K key, V value) {
        if (!writing) {
             valuesFile.close();
             valuesFile.appendMode();
            writing = true;
        }
        keyPositionFile.write(keySerialiser.serialise(key));
        keyPositionFile.write(currentPositionInValuesFile.toString());
        currentPositionInValuesFile +=  valuesFile.write(valueSerialiser.serialise(value));
    }

    private void flushDeletes() {
        if (!deleteKeyFile.exist()) {
            deleteKeyFile.createFile();
        }
        for (K entry : deleteKeySet) {
            deleteKeyFile.write(keySerialiser.serialise(entry));
        }
        deleteKeyFile.writeSubmit();
        deleteKeySet.clear();
    }
}
