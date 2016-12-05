package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 24.11.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class OptimizedHashTable<K, V> implements KeyValueStorage<K, V> {
    private static final long TO_INSERT = -1;
    private static final int MIN_NUMBER_TO_INSERT = 100;

    private final String databaseName = "storage.db";
    private final String databasePath;
    private final String keysFileName = "keys_" + databaseName;
    private final String valuesFileName = "values_" + databaseName;

    private final SerializationStrategy<K> keySerializer;
    private final SerializationStrategy<V> valueSerializer;

    private final Map<K, V> elemsToInsert = new HashMap<>();

    private RandomAccessFile keysFile;
    private RandomAccessFile valuesFile;
    private boolean closed = false;
    private Map<K, Long> offsets = new HashMap<>();
    private int optimizeCounter = 0;

    public OptimizedHashTable(String path,
                              SerializationStrategy<K> serializerKeys,
                              SerializationStrategy<V> serializerValues)
            throws IOException {

        keySerializer = serializerKeys;
        valueSerializer = serializerValues;
        databasePath = path + File.separator;
        File databaseFile;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            databaseFile = new File(databasePath + databaseName);
        } else {
            throw new IllegalArgumentException("path" + path + " is not available");
        }

        elemsToInsert.clear();
        if (databaseFile.createNewFile()) {
            createStorage();
        } else {
            openStorage();
        }
    }

    private synchronized void createStorage() throws IOException {
        File fileK = new File(databasePath + keysFileName);
        File fileV = new File(databasePath + valuesFileName);

        keysFile = new RandomAccessFile(fileK, "rw");
        valuesFile = new RandomAccessFile(fileV, "rw");
    }

    private void openStorage() throws IOException {
        File fileK = new File(databasePath + keysFileName);
        File fileV = new File(databasePath + valuesFileName);

        if (!fileK.exists() || !fileV.exists()) {
            throw new IOException("database does not exist");
        }

        keysFile = new RandomAccessFile(fileK, "rw");
        valuesFile = new RandomAccessFile(fileV, "rw");

        getKeysAndOffsets();
    }

    private void getKeysAndOffsets() throws IOException {
        int numberOfKeys = (new IntegerSerializator()).read(keysFile);
        for (int i = 0; i < numberOfKeys; i++) {
            K key = keySerializer.read(keysFile);
            Long offset = (new LongSerializator()).read(keysFile);
            offsets.put(key, offset);
        }
    }

    @Override
    public synchronized V read(K key) {
        checkForClosed();
        Long offset = offsets.get(key);
        if (offset == null) {
            return null;
        }
        try {
            if (offset == TO_INSERT) {
                return elemsToInsert.get(key);
            } else {
                valuesFile.seek(offset);
                return valueSerializer.read(valuesFile);
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        checkForClosed();
        return offsets.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        checkForClosed();
        try {
            elemsToInsert.put(key, value);
            Long offset = offsets.put(key, TO_INSERT);

            if (offset == null || offset == TO_INSERT) {
                optimizeCounter += 1;
            }
            optimize();
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong during writing");
        }
    }

    @Override
    public synchronized void delete(K key) {
        checkForClosed();
        if (offsets.containsKey(key)) {
            if (offsets.remove(key) != TO_INSERT) {
                optimizeCounter += 1;
            }
            elemsToInsert.remove(key);
        }
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        checkForClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        checkForClosed();
        return offsets.size();
    }

    private synchronized void checkForClosed() {
        if (closed) {
            throw new IllegalStateException("database is closed");
        }
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            if (closed) {
                return;
            }
            insertElems();
            keysFile.seek(0);
            keysFile.setLength(0);
            new IntegerSerializator().write(keysFile, offsets.size());
            SerializationStrategy<Long> longSerializator = new LongSerializator();
            for (K key : offsets.keySet()) {
                keySerializer.write(keysFile, key);
                longSerializator.write(keysFile, offsets.get(key));
            }
            offsets.clear();
            keysFile.close();
            valuesFile.close();
            closed = true;
        } catch (IOException e) {
            throw new IOException("soemthing went wrong while closing");
        }
    }

    private synchronized void optimize() throws IOException {
        // Если нужно вставить много элементов в б.д.
        if (elemsToInsert.size() > MIN_NUMBER_TO_INSERT) {
            insertElems();
        }

        // Если нужно убрать много элементов из б.д.
        if (optimizeCounter > offsets.size()) {
            try {
                Map<K, Long> newOffsets = new HashMap<>();
                String newValuesPath = databasePath + File.separator + "new_" + valuesFileName;

                String valuesPath = databasePath + File.separator + valuesFileName;
                File newValues = new File(newValuesPath);

                try (RandomAccessFile newValuesFile = new RandomAccessFile(newValues, "rw")) {
                    newValuesFile.seek(0);
                    newValuesFile.setLength(0);

                    for (K key : offsets.keySet()) {
                        if (offsets.get(key) == TO_INSERT) {
                            newOffsets.put(key, TO_INSERT);
                        } else {
                            valuesFile.seek(offsets.get(key));
                            V value = valueSerializer.read(valuesFile);
                            newOffsets.put(key, newValuesFile.length());
                            valueSerializer.write(newValuesFile, value);
                        }
                    }
                    optimizeCounter = 0;
                    offsets = newOffsets;
                    valuesFile.close();
                    newValuesFile.close();
                    Files.move(Paths.get(newValuesPath), Paths.get(valuesPath), REPLACE_EXISTING);
                    valuesFile = new RandomAccessFile(valuesPath, "rw");
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    private synchronized void insertElems() throws IOException {
        Long offset = valuesFile.length();
        valuesFile.seek(offset);
        for (K key : elemsToInsert.keySet()) {
            offsets.put(key, offset);
            valueSerializer.write(valuesFile, elemsToInsert.get(key));
            offset = valuesFile.getFilePointer();
        }
        elemsToInsert.clear();
    }
}

