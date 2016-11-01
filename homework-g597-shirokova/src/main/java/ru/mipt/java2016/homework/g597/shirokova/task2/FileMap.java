package ru.mipt.java2016.homework.g597.shirokova.task2;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

class FileMap<K, V> {

    private final HashMap<K, V> storageMap = new HashMap<>();
    private final String pathToStorage;
    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;
    private boolean isClosed = false;
    private File lock;

    FileMap(String currentPath, SerializationStrategy<K> serializerForKeys,
            SerializationStrategy<V> serializerForValues) throws IOException {
        keySerializer = serializerForKeys;
        valueSerializer = serializerForValues;
        File fileForStorage = new File(currentPath);
        if (!fileForStorage.exists()) {
            throw new IOException("Can't create file");
        }
        pathToStorage = currentPath + "/storage.db";
        fileForStorage = new File(pathToStorage);
        fileForStorage.createNewFile();
        checkAccess(currentPath);
        readMapFromFile(fileForStorage);
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("File is closed");
        }
    }

    private void checkAccess(String path) throws IOException {
        lock = new File(path + "/lock.db");
        if (!lock.createNewFile()) {
            throw new IllegalStateException("Storage is already used");
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
        writeMapToFile();
        isClosed = true;
        storageMap.clear();
        lock.delete();
    }

    private void readMapFromFile(File fileForStorage) throws IOException {
        try (FileInputStream fileStream = new FileInputStream(fileForStorage);
             BufferedInputStream bufferedStream = new BufferedInputStream(fileStream);
             DataInputStream fileInput = new DataInputStream(bufferedStream)) {
            while (fileInput.available() != 0) {
                storageMap.put(
                        keySerializer.deserialize(fileInput),
                        valueSerializer.deserialize(fileInput)
                );
            }
        }
    }

    private void writeMapToFile() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream((pathToStorage));
             BufferedOutputStream bufferedStream = new BufferedOutputStream(outputStream);
             DataOutputStream outputFile = new DataOutputStream(bufferedStream)) {
            for (HashMap.Entry<K, V> entry : storageMap.entrySet()) {
                keySerializer.serialize(outputFile, entry.getKey());
                valueSerializer.serialize(outputFile, entry.getValue());
            }
        }
    }

}
