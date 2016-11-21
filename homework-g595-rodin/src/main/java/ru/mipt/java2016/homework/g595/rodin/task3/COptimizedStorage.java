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

    private boolean pointerOnEnd = false;

    private boolean closeFlag = false;

    private long currentOffset = 0;

    public COptimizedStorage(String directoryPath, ISerialize<KeyType> keySerializer,
                             ISerialize<ValueType> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        String directory = buildDirectory(directoryPath);
        databaseFile = new CFileHandler(directory + "storage.db");
        configurationFile = new CFileHandler(directory + "config.db");
        if (!configurationFile.exists()) {
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
            key = configurationFile.readNextToken();
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
        checkClosed();
        Long offset = keyMap.get(key);
        return loadKey(offset);
    }

    @Override
    public boolean exists(KeyType key) {
        checkClosed();
        return keyMap.containsKey(key);
    }

    @Override
    public void write(KeyType key, ValueType value) {
        checkClosed();
        keyMap.put(key, currentOffset);
        writeToDisk(value);
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
        checkClosed();
        keyMap.remove(key);
    }

    @Override
    public Iterator<KeyType> readKeys() {
        checkClosed();
        return keyMap.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return keyMap.size();
    }

    @Override
    public void close() throws IOException {
        rewriteConfiguration();
        closeFlag = true;
    }

    private void rewriteConfiguration() {
        configurationFile.close();
        configurationFile.delete();
        configurationFile.createFile();
        for (KeyType item : keyMap.keySet()) {
            configurationFile.write(keySerializer.serialize(item));
            configurationFile.write(longSerializer.serialize(keyMap.get(item)));
        }
        configurationFile.close();
    }
}
