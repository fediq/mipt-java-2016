package ru.mipt.java2016.homework.g595.nosareva.task2;


import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by maria on 25.10.16.
 */
class SimpleStorageTable<K, V> {

    private File source;
    private final String pathToFile;
    private int size = 0;

    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private boolean closed = false;

    private final HashMap<K, V> keyValueMap = new HashMap<>();

    private void getKeysAndValues() throws IOException {
        try {
            DataInputStream fileInput  = new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
            size = fileInput.readInt();
            for (int i = 0; i < size; i++) {
                K key = keySerializer.deserializeFromStream(fileInput);
                V value = valueSerializer.deserializeFromStream(fileInput);
                keyValueMap.put(key, value);
            }

            fileInput.close();
        } catch (IOException ioexcep) {
            System.out.println(ioexcep.getMessage());
        }
    }

    SimpleStorageTable(String path, Serializer<K> serializerForKeys, Serializer<V> serializerForValues) {

        this.keySerializer = serializerForKeys;
        this.valueSerializer = serializerForValues;

        File receivedFile = new File(path);
        if (receivedFile.exists() && receivedFile.isDirectory()) {
            this.pathToFile = path + "/storage.db";
            this.source = new File(pathToFile);
        } else {
            throw new RuntimeException("path" + path + " isn't available");
        }

        try {
            if (source.exists()) {
                getKeysAndValues();
            }
        } catch (IOException except) {
            System.out.println(except.getMessage());
        }
    }

    private void chekingForClosed() {
        if (closed) {
            throw new IllegalStateException("File has been closed");
        }
    }

    public V read(K key) {
        chekingForClosed();
        if (!keyValueMap.containsKey(key)) {
            return null;
        }
        return keyValueMap.get(key);
    }

    public boolean exists(K key) {
        chekingForClosed();
        return keyValueMap.containsKey(key);
    }

    public void write(K key, V value) {
        chekingForClosed();
        if (keyValueMap.containsKey(key)) {
            keyValueMap.replace(key, value);
        } else {
            keyValueMap.put(key, value);
            size += 1;
        }
    }

    public void delete(K key) {
        if (keyValueMap.containsKey(key)) {
            size -= 1;
        }
        keyValueMap.remove(key);
    }

    public int getSize() {
        return size;
    }

    Iterator<K> readKeys() {
        chekingForClosed();
        return keyValueMap.keySet().iterator();
    }

    public void close() {
        if (closed) {
            return;
        }

        try {
            DataOutputStream fileOutput = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(pathToFile)));
            fileOutput.writeInt(keyValueMap.size());

            for (Map.Entry<K, V> entry : keyValueMap.entrySet()) {
                keySerializer.serializeToStream(entry.getKey(), fileOutput);
                valueSerializer.serializeToStream(entry.getValue(), fileOutput);
            }

            fileOutput.close();
            closed = true;

        } catch (IOException except) {
            System.out.println(except.getMessage());
        }
    }
}
