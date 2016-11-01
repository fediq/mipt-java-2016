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
    private File flag;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private Map<K, V> dataBase;

    public MyKeyValueStorage(String path, Serializer<K> keySerializerArg,
                             Serializer<V> valueSerializerArg) throws IOException {
        flag = Paths.get(path, "flag").toFile();
        if (!flag.createNewFile()) {
            throw new RuntimeException("File has already been opened");
        }

        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        dataBase = new HashMap<>();
        File pathToFile = Paths.get(path, "storage.db").toFile();

        try {
            pathToFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create the file");
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
        checkState();
        return dataBase.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkState();
        return dataBase.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkState();
        dataBase.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkState();
        dataBase.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkState();
        return dataBase.keySet().iterator();
    }

    @Override
    public int size() {
        checkState();
        return dataBase.size();
    }

    @Override
    public void close() throws IOException {
        checkState();
        saveChanges();
        dataBase = null;
        file.close();
        flag.delete();
    }

    private void checkState() {
        if (dataBase == null) {
            throw new RuntimeException("Already closed");
        }
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

    private void saveChanges() throws IOException {
        file.setLength(0);
        file.seek(0);
        for (Map.Entry<K, V> entry : dataBase.entrySet()) {
            keySerializer.write(file, entry.getKey());
            valueSerializer.write(file, entry.getValue());
        }
    }
}
