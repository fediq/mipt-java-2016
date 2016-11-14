package ru.mipt.java2016.homework.g594.rubanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by king on 30.10.16.
 */

public class MyKeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {
    /* ! path to the directory */
    private String nameOfFile;
    /* ! string used to validate if the storage is appropriate */
    private static final String VALIDATION = "validationstring";
    /* ! storage where data will be stored before closing */
    private HashMap<K, V> storage;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;
    private boolean isOpened;

    public MyKeyValueStorageImpl(String nameOfFileTmp,
                                 MySerializer keySerializerTmp, MySerializer valueSerializerTmp) {
        nameOfFile = nameOfFileTmp + File.separator + "storage.db";
        keySerializer = keySerializerTmp;
        valueSerializer = valueSerializerTmp;
        storage = new HashMap<K, V>();
        isOpened = true;

        /* ! If there is no storage - we create it
         * ! If there is one - we use it: we copy all of it's data and work with it locally
         */
        File path = new File(nameOfFile);
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create a new file");
            }

            try (DataOutputStream Output = new DataOutputStream(new FileOutputStream(nameOfFile))) {
                Output.writeUTF(VALIDATION);
                Output.writeInt(0);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to write to file");
            }
        }

        try (DataInputStream Input = new DataInputStream(new FileInputStream(nameOfFile))) {
            if (!Input.readUTF().equals(VALIDATION)) {
                throw new IllegalStateException("Validation failed");
            }
            int amount = Input.readInt();
            for (int i = 0; i < amount; ++i) {
                K keyToInsert = keySerializer.deserializeFromStream(Input);
                V valueToInsert = valueSerializer.deserializeFromStream(Input);
                storage.put(keyToInsert, valueToInsert);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read from file");
        }
    }

    @Override
    public V read(K key) {
        isFileClosed();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        isFileClosed();
        return storage.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        isFileClosed();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        isFileClosed();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        isFileClosed();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        isFileClosed();
        return storage.size();
    }

    /* ! We write to file before closing */
    @Override
    public void close() throws IOException {
        isFileClosed();
        try (DataOutputStream Output = new DataOutputStream(new FileOutputStream(nameOfFile))) {
            Output.writeUTF(VALIDATION);
            Output.writeInt(storage.size());
            for (Map.Entry<K, V> i: storage.entrySet()) {
                keySerializer.serializeToStream(Output, i.getKey());
                valueSerializer.serializeToStream(Output, i.getValue());
            }
            isOpened = false;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save and close storage");
        }
    }

    private void isFileClosed() {
        if (!isOpened) {
            throw new IllegalStateException("It's impossible to work with a closed storage");
        }
    }
}