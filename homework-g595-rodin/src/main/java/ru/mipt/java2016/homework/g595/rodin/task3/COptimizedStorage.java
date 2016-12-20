package ru.mipt.java2016.homework.g595.rodin.task3;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import ru.mipt.java2016.homework.g595.rodin.task2.Serializer.CSerializeLong;
import ru.mipt.java2016.homework.g595.rodin.task3.Serializer.ISerialize;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class COptimizedStorage<KeyType, ValueType> implements KeyValueStorage<KeyType, ValueType> {

    private final HashMap<KeyType, Long> keyMap = new HashMap<>();

    private final ISerialize<KeyType> keySerializer;

    private final ISerialize<ValueType> valueSerializer;

    private final CSerializeLong longSerializer = new CSerializeLong();

    private final CFileHandler databaseFile;

    private final CFileHandler configurationFile;

    private final CFileHandler lockFile;

    private final CFileHandler validationFile;

    private boolean pointerOnEnd = false;

    private boolean closeFlag = false;

    private long currentOffset = 0;

    private boolean newDatabase = false;

    public COptimizedStorage(String directoryPath, ISerialize<KeyType> keySerializer,
                             ISerialize<ValueType> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        String directory = buildDirectory(directoryPath);
        databaseFile = new CFileHandler(directory + "storage.db");
        configurationFile = new CFileHandler(directory + "config.db");
        lockFile = new CFileHandler(directory + "lock.db");
        validationFile = new CFileHandler(directory + "valid.db");
        if (lockFile.exists()) {
            throw new RuntimeException("found another worker");
        } else {
            lockFile.createFile();
        }

        if (!validationFile.exists()) {
            newDatabase = true;
            validationFile.createFile();
            configurationFile.createFile();
            databaseFile.createFile();
        }
        currentOffset = databaseFile.length();
        databaseFile.appMode();
        pointerOnEnd = true;
        initialize();

    }

    private String buildDirectory(String path) {
        if (path == null || path.equals("")) {
            return "";
        }
        return path + File.separator;
    }

    private void initialize() {
        String key = configurationFile.readNextToken();
        while (key != null) {
            Long offset = longSerializer.deserialize(configurationFile.readNextToken());
            keyMap.put(keySerializer.deserialize(key), offset);
            configurationFile.addToCheckSum(key.getBytes());
            configurationFile.addToCheckSum(longSerializer.serialize(offset).getBytes());

            key = configurationFile.readNextToken();
        }
        if (!newDatabase) {
            long checkSum = configurationFile.getCheckSum();
            long sum = Long.parseLong(validationFile.readNextToken());
            if (checkSum != sum) {
                throw new RuntimeException("validation error");
            }
        }

        configurationFile.close();
        configurationFile.appMode();
    }

    private void checkClosed() {
        if (closeFlag) {
            throw new RuntimeException("Closed");
        }
    }

    private ValueType loadKey(Long offset) {
        if (offset == null) {
            return null;
        }
        if (pointerOnEnd) {
            databaseFile.close();
            pointerOnEnd = false;
        }
        databaseFile.reposition(offset);
        return valueSerializer.deserialize(databaseFile.readNextToken());
    }


    @Override
    public ValueType read(KeyType key) {
        synchronized (configurationFile) {
            checkClosed();
            Long offset = keyMap.get(key);
            return loadKey(offset);
        }
    }

    @Override
    public boolean exists(KeyType key) {
        synchronized (configurationFile) {
            checkClosed();
            return keyMap.containsKey(key);
        }
    }

    @Override
    public void write(KeyType key, ValueType value) {
        synchronized (configurationFile) {
            checkClosed();
            keyMap.put(key, currentOffset);
            writeToDisk(value);
        }
    }

    private void writeToDisk(ValueType value) {
        if (!pointerOnEnd) {
            databaseFile.close();
            databaseFile.appMode();
            pointerOnEnd = true;
        }
        currentOffset += databaseFile.write(valueSerializer.serialize(value));
    }

    @Override
    public void delete(KeyType key) {
        synchronized (configurationFile) {
            checkClosed();
            keyMap.remove(key);
        }
    }

    @Override
    public Iterator<KeyType> readKeys() {
        synchronized (configurationFile) {
            checkClosed();
            return keyMap.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (configurationFile) {
            checkClosed();
            return keyMap.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (configurationFile) {
            if (!closeFlag) {
                rewriteConfiguration();
                closeFlag = true;
                lockFile.delete();
            }
        }
    }

    private void rewriteConfiguration() {
        configurationFile.close();
        configurationFile.delete();

        configurationFile.createFile();
        for (KeyType item : keyMap.keySet()) {
            configurationFile.write(keySerializer.serialize(item));
            configurationFile.write(longSerializer.serialize(keyMap.get(item)));
            configurationFile.addToCheckSum(keySerializer.serialize(item).getBytes());
            configurationFile.addToCheckSum(longSerializer.serialize(keyMap.get(item)).getBytes());
        }
        validationFile.delete();
        validationFile.createFile();
        validationFile.write(String.valueOf(configurationFile.getCheckSum()));
        validationFile.close();
        configurationFile.close();
    }
}

