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
    public final HashMap<K, V> cache = new HashMap<>();
    private String filePath;
    private boolean closed = false;

    public KVSImplementation(String directoryPath, KVSSerializationInterface<K> keySerialization,
                             KVSSerializationInterface<V> valueSerialization) {
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        filePath = directoryPath + FILENAME;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("File hasn't been created");
            }
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath))) {
                out.writeUTF(FLAGSTRING);
                out.writeInt(0);
            } catch (IOException e) {
                throw new IllegalStateException("Failed at writing to file");
            }
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            if (!in.readUTF().equals(FLAGSTRING)) {
                throw new IllegalStateException("Not a valid file");
            }
            int amount = in.readInt();
            for (int i = 0; i < amount; ++i) {
                K key = keySerialization.deserialize(in);
                V value = valueSerialization.deserialize(in);
                cache.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read from to file");
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
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath))) {
            out.writeUTF(FLAGSTRING);
            out.writeInt(cache.size());
            for (Map.Entry<K, V> entry: cache.entrySet()) {
                keySerialization.serialize(out, entry.getKey());
                valueSerialization.serialize(out, entry.getValue());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed at writing storage to file");
        }
        cache.clear();
    }

    public boolean isStorageClosed() {
        return this.closed;
    }
}