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

    private HashMap<K, V> storage = new HashMap<>();
    private Serialization<K> keySerialization;
    private Serialization<V> valueSerialization;
    private File keys;
    private File values;
    private File procAccess;
    private static final String TRY_PROC = "tryproc";
    private static final int KEYS_SECRET_HASH = 47;
    private static final int VALUES_SECRET_HASH = 31;
    private boolean flag;

    public KlabertancStorage(String path, Serialization<K> k, Serialization<V> v) {

        keySerialization = k;
        valueSerialization = v;

        File dir = new File(path);
        if (!dir.isDirectory() || !dir.exists()) {
            throw new RuntimeException("Invalid path!");
        }

        procAccess = new File(dir, TRY_PROC);
        if (procAccess.exists()) {
            throw new RuntimeException("Another process is already running!");
        }
        procAccess.mkdir();

        keys = new File(dir, "keys.db");
        values = new File(dir, "values.db");

        if (keys.exists() && values.exists()) {
            get();
        }
    }

    private void get() {
        try (DataInputStream keysInput = new DataInputStream(new FileInputStream(keys));
             DataInputStream valuesInput = new DataInputStream(new FileInputStream(values))) {
            Integer keysValid = keysInput.readInt();
            Integer valuesValid = valuesInput.readInt();
            if ((KEYS_SECRET_HASH != keysValid) || (VALUES_SECRET_HASH != valuesValid)) {
                throw new RuntimeException("It's not a KlabertancStorage!");
            }
            
            int size = keysInput.readInt();

            for (int i = 0; i < size; i++) {
                K key = keySerialization.read(keysInput);
                V value = valueSerialization.read(valuesInput);
                storage.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Wrong input/output!");
        }
    }

    private void put() throws IOException {
        try (DataOutputStream keysOutput = new DataOutputStream(new FileOutputStream(keys));
             DataOutputStream valuesOutput = new DataOutputStream(new FileOutputStream(values))) {
            keysOutput.writeInt(KEYS_SECRET_HASH);
            valuesOutput.writeInt(VALUES_SECRET_HASH);
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

    private void closed() {
        if (flag) {
            throw new RuntimeException("You're a bad guy. Don't try to access the closed storage!");
        }
    }

    @Override
    public V read(K key) {
        closed();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        closed();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        closed();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        closed();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        closed();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        closed();
        return storage.size();
    }

    @Override
    public void close() throws IOException {
        closed();
        put();
        procAccess.delete();
        flag = true;
    }
}
