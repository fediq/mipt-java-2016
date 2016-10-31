package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class KlabertancStorage<K, V> implements KeyValueStorage<K, V> {

    private static final String TRY_LOCK = "trylock";
    private static final Integer KEYS_SECRET_NUMBER = 47;
    private static final Integer VALUES_SECRET_NUMBER = 31;

    private HashMap<K, V> storage = new HashMap<>();
    private Serialization<K> keySerialization;
    private Serialization<V> valueSerialization;
    private File keys;
    private File values;
    private File lockAccess;
    private boolean flagForClose;

    public KlabertancStorage(String path, Serialization<K> k, Serialization<V> v) {

        keySerialization = k;
        valueSerialization = v;

        File dir = new File(path);
        if (!dir.isDirectory() || !dir.exists()) {
            throw new RuntimeException("Invalid path!");
        }

        lockAccess = new File(dir, TRY_LOCK);
        if (lockAccess.exists()) {
            throw new RuntimeException("Another process is already running!");
        }
        lockAccess.mkdir();

        keys = new File(dir, "keys.db");
        values = new File(dir, "values.db");

        if (keys.exists() && values.exists()) {
            getStorageFromDisk();
        }

        flagForClose = false;
    }

    private void getStorageFromDisk() {
        try (DataInputStream keysInput = new DataInputStream(new FileInputStream(keys));
             DataInputStream valuesInput = new DataInputStream(new FileInputStream(values))) {
            int keysValid = keysInput.readInt();
            int valuesValid = valuesInput.readInt();
            if (!KEYS_SECRET_NUMBER.equals(keysValid) || !VALUES_SECRET_NUMBER.equals(valuesValid)) {
                throw new RuntimeException("It's not a KlabertancStorage!");
            }
            
            int size = keysInput.readInt();

            for (int i = 0; i < size; i++) {
                K key = keySerialization.read(keysInput);
                V value = valueSerialization.read(valuesInput);
                storage.put(key, value);
            }

            keysInput.close();
            valuesInput.close();
        } catch (IOException e) {
            throw new RuntimeException("Wrong input/output!");
        }
    }

    private void putStorageOnDisk() throws IOException {
        try (DataOutputStream keysOutput = new DataOutputStream(new FileOutputStream(keys));
             DataOutputStream valuesOutput = new DataOutputStream(new FileOutputStream(values))) {
            keysOutput.writeInt(KEYS_SECRET_NUMBER);
            valuesOutput.writeInt(VALUES_SECRET_NUMBER);
            keysOutput.writeInt(storage.size());
            for (Map.Entry<K, V> entry : storage.entrySet()) {
                keySerialization.write(keysOutput, entry.getKey());
                valueSerialization.write(valuesOutput, entry.getValue());
            }
            keysOutput.close();
            valuesOutput.close();
        } catch (IOException e) {
            throw new RuntimeException("Wrong input/output!");
        }
    }

    private void isStorageClosed() {
        if (flagForClose) {
            throw new RuntimeException("You're a bad guy. Don't try to access the closed storage!");
        }
    }

    @Override
    public V read(K key) {
        isStorageClosed();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        isStorageClosed();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isStorageClosed();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        isStorageClosed();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        isStorageClosed();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        isStorageClosed();
        return storage.size();
    }

    @Override
    public void close() throws IOException {
        isStorageClosed();
        putStorageOnDisk();
        lockAccess.delete();
        flagForClose = true;
    }
}
