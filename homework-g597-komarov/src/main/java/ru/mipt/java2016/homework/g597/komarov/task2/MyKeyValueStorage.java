package ru.mipt.java2016.homework.g597.komarov.task2;

/**
 * Created by Михаил on 29.10.2016.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.RandomAccessFile;

import  ru.mipt.java2016.homework.base.task2.KeyValueStorage;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private RandomAccessFile file;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private Map<K, V> dataBase;

    public MyKeyValueStorage(String path, Serializer<K> keySerializerArg,
                             Serializer<V> valueSerializerArg) throws IOException {
        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        dataBase = new HashMap<>();
        File pathToFile = Paths.get(path, "storage.db").toFile();

        if(!pathToFile.exists()) {
            try {
                if (!pathToFile.createNewFile()) {
                    throw new RuntimeException("File has already created");
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create the file");
            }
        }
        try {
            file = new RandomAccessFile(pathToFile, "rw");
            dataBase = readMapFromFile();
        } catch (FileNotFoundException e) {
            throw new IOException("File not found");
        }
    }

    @Override
    public V read(K key) {
        if (ifOpen()) {
            return dataBase.get(key);
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public boolean exists(K key) {
        if (ifOpen()) {
            return dataBase.containsKey(key);
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public void write(K key, V value) {
        if (ifOpen()) {
            dataBase.put(key, value);
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public void delete(K key) {
        if (ifOpen()) {
            dataBase.remove(key);
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (ifOpen()) {
            return dataBase.keySet().iterator();
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public int size() {
        if (ifOpen()) {
            return dataBase.size();
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (ifOpen()) {
            saveChanges();
            dataBase = null;
            file.close();
        } else {
            throw new RuntimeException("Already closed");
        }
    }

    private boolean ifOpen() {
        return (dataBase != null);
    }

    private Map<K, V> readMapFromFile() throws IOException {
        Map<K, V> bufMap = new HashMap<>();
        K key;
        V value;
        file.seek(0);
        while (file.getFilePointer() < file.length()) {
            key = keySerializer.read(file);
            value = valueSerializer.read(file);
            bufMap.put(key, value);
        }
        return bufMap;
    }

    private void saveChanges () throws IOException {
        file.setLength(0);
        file.seek(0);
        for (Map.Entry<K, V> entry : dataBase.entrySet()) {
            keySerializer.write(file, entry.getKey());
            valueSerializer.write(file, entry.getValue());
        }
    }
 }
