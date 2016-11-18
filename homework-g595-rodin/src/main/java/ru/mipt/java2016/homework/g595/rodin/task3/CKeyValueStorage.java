package ru.mipt.java2016.homework.g595.rodin.task3;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.ISerialize;

import java.io.File;
import java.io.IOException;
import java.util.*;



public class CKeyValueStorage<KeyType, ValueType> implements KeyValueStorage<KeyType, ValueType> {

    private final HashMap<KeyType, Long> keyMap = new HashMap<>();

    private final ISerialize<KeyType> keySerializer;

    private final ISerialize<ValueType> valueSerializer;

    private final CFileHandler databaseFile;

    private final CFileHandler configurationFile;

    private String directory;

    private int deleted = 0;

    private int updatedCounter = 0;

    private boolean closeFlag = false;

    private LoadingCache<KeyType,ValueType> loadingCache = CacheBuilder.newBuilder()
            .softValues()
            .build(
                    new CacheLoader<KeyType, ValueType>() {
                        @Override
                        public ValueType load(KeyType key) throws CacheException {
                            ValueType result = loadValue(key);
                            if(result == null){
                                throw new CacheException("no such key");
                            }
                            return result;
                        }
                    });


    private ValueType loadValue (KeyType key) {
        if(!exists(key)) {
            return null;
        }
        long offset = keyMap.get(key);
        return valueSerializer.deserialize(databaseFile.loadKey(offset));
    }

    public CKeyValueStorage(String directoryPath , ISerialize<KeyType> keySerializer,
                            ISerialize<ValueType> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        directory = buildDirectory(directoryPath);
        databaseFile = new CFileHandler(directory + "storage.db");
        configurationFile = new CFileHandler(directory + "config.db");

    }

    private String buildDirectory(String directory){
        if(directory.length() > 0){
            return directory + File.separator;
        } else {
            return "";
        }
    }


    private void checkIfClosed() {
        if(closeFlag == true) {
            throw new RuntimeException("Closed");
        }

    }

    @Override
    public ValueType read(KeyType key) {
        synchronized (keyMap) {
            checkIfClosed();
            return loadValue(key);
        }
    }

    @Override
    public boolean exists(KeyType key) {
        synchronized (keyMap) {
            checkIfClosed();
            return keyMap.containsKey(key);
        }
    }

    @Override
    public void write(KeyType key, ValueType value) {
        synchronized (keyMap) {
            checkIfClosed();
            updatedCounter++;
            long offset =  databaseFile.append(valueSerializer.serialize(value));
            keyMap.put(key,offset);
        }
    }

    @Override
    public void delete(KeyType key) {
        synchronized (keyMap) {
            checkIfClosed();
            deleted++;
            keyMap.remove(key);
        }

    }

    @Override
    public Iterator<KeyType> readKeys() {
        synchronized (keyMap) {
            checkIfClosed();
            return keyMap.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (keyMap) {
            checkIfClosed();
            return keyMap.size();
        }
    }

    @Override
    public void close() throws IOException {

    }
}
