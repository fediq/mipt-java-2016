package ru.mipt.java2016.homework.g594.islamov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iskander Islamov on 29.10.2016.
 */

public class KVSImplementation<K, V> implements KeyValueStorage<K, V> {

    private static final String FILENAME = "/storage.db";
    private static final String FLAGSTRING = "ITISMYFILE";

    public final KVSSerializationInterface<K> keySerialization;
    public final KVSSerializationInterface<V> valueSerialization;
    public final HashMap<K, V> cache = new HashMap<K, V>();
    private File file;
    private boolean closed = false;

    public KVSImplementation(String directoryPath, KVSSerializationInterface<K> keySerialization,
                             KVSSerializationInterface<V> valueSerialization) {
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        file = new File(directoryPath + FILENAME);
        try {
            if (file.exists()) {
                if (!isValidFile()) {
                    throw new RuntimeException("Invalid File");
                }
            }
        } catch (FileNotFoundException except) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public V read(K key) {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        return cache.get(key);
    }

    @Override
    public boolean exists(K key) {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        return cache.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        cache.put(key, value);
    }

    @Override
    public void delete(K key) {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        cache.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        return cache.keySet().iterator();
    }

    @Override
    public int size() {
        if (isStorageClosed()) {
            throw new RuntimeException("File has already been closed");
        }
        return cache.size();
    }

    @Override
    public void close() throws FileNotFoundException {
        closed = true;
        StringBuilder storageInString = new StringBuilder(FLAGSTRING + "\n");
        for (Map.Entry<K, V> entry : cache.entrySet()) {
            storageInString.append(keySerialization.serialize(entry.getKey()));
            storageInString.append('\n');
            storageInString.append(valueSerialization.serialize(entry.getValue()));
            storageInString.append('\n');
        }
        writeToFile(storageInString.toString());
        cache.clear();
    }

    public boolean isValidFile() throws FileNotFoundException {
        String storageInString = readFromFile();
        String[] splitedStorageInString = storageInString.split("\n");
        if (splitedStorageInString.length > 0 && !splitedStorageInString[0].equals(FLAGSTRING)) {
            return false;
        }
        cache.clear();
        int storageSize = splitedStorageInString.length;
        for (int i = 1; i < storageSize; i += 2) {
            try {
                K stringToKeyType = keySerialization.deserialize(splitedStorageInString[i]);
                V stringToValueType = valueSerialization.deserialize(splitedStorageInString[i + 1]);
                cache.put(stringToKeyType, stringToValueType);
            } catch (BadStorageException e) {
                return false;
            }
        }
        return true;
    }

    public String readFromFile() throws FileNotFoundException {
        StringBuilder storageInString = new StringBuilder();
        if (!getFile().exists()) {
            throw new FileNotFoundException("File is not found");
        }
        try {
            BufferedReader readLines = new BufferedReader(new FileReader(getFile().getAbsoluteFile()));
            try {
                String inputLine;
                while ((inputLine = readLines.readLine()) != null) {
                    storageInString.append(inputLine);
                    storageInString.append("\n");
                }
            } finally {
                readLines.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return storageInString.toString();
    }

    public void writeToFile(String storageInString) throws FileNotFoundException {
        try {
            if (!getFile().exists()) {
                file.createNewFile();
            }
            PrintWriter outputLines = new PrintWriter(getFile().getAbsoluteFile());
            try {
                outputLines.write(storageInString);
            } finally {
                outputLines.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return this.file;
    }

    public boolean isStorageClosed() {
        return this.closed;
    }
}