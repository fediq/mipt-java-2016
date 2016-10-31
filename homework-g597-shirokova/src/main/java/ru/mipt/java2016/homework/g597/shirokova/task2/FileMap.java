package ru.mipt.java2016.homework.g597.shirokova.task2;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

class FileMap<K, V> {

    private final HashMap<K, V> storageMap = new HashMap<>();
    private final String pathToStorage;
    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;
    private boolean isClosed;

    FileMap(String currentPath, SerializationStrategy<K> serializerForKeys,
            SerializationStrategy<V> serializerForValues) throws IOException {
        keySerializer = serializerForKeys;
        valueSerializer = serializerForValues;
        File fileForStorage = new File(currentPath);
        pathToStorage = currentPath + "/storage.db";
        if (fileForStorage.exists()) {
            fileForStorage = new File(pathToStorage);
        } else {
            throw new IOException("Can't create file");
        }
        if (fileForStorage.exists()) {
            try {
                DataInputStream fileInput = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(fileForStorage)));
                while (fileInput.available() != 0) {
                    storageMap.put(
                            keySerializer.deserialize(fileInput),
                            valueSerializer.deserialize(fileInput)
                    );
                }
                isClosed = false;
                fileInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("File is closed");
        }
    }

    V read(K key) {
        checkClosed();
        return storageMap.get(key);
    }

    boolean exists(K key) {
        checkClosed();
        return storageMap.containsKey(key);
    }

    void write(K key, V value) {
        checkClosed();
        storageMap.put(key, value);
    }

    void delete(K key) {
        checkClosed();
        storageMap.remove(key);
    }

    Iterator<K> readKeys() {
        checkClosed();
        return storageMap.keySet().iterator();
    }

    int size() {
        checkClosed();
        return storageMap.size();
    }

    void close() throws IOException {
        checkClosed();
        DataOutputStream outputFile = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(pathToStorage)));
        for (HashMap.Entry<K, V> entry : storageMap.entrySet()) {
            keySerializer.serialize(outputFile, entry.getKey());
            valueSerializer.serialize(outputFile, entry.getValue());
        }
        isClosed = true;
        storageMap.clear();
        outputFile.close();
    }
}
