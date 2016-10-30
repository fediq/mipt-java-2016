package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map = new HashMap<>();
    private File values;
    private File keys;
    private static final String TRY_PROC = "Some process is already working :(";
    private static final String VALUES_VALIDATION = "This is a Values storage.";
    private static final String KEYS_VALIDATION = "This is a Keys storage.";
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private File procAccess;
    boolean closedAccess;

    private void isClosed() {
        if (closedAccess) {
            throw new RuntimeException("You're wrong. Don't try to access closed storage!");
        }
    }

    public MyKeyValueStorage(String path, MySerialization<K> k, MySerialization<V> v) throws IOException {

        procAccess = new File(TRY_PROC);

        if (procAccess.createNewFile()) {
            keySerialization = k;
            valueSerialization = v;

            File dir = new File(path);
            if (!dir.isDirectory() || !dir.exists()) {
                throw new RuntimeException("This directory is already exists or it is not a directory!");
            }

            values = new File(dir, "values.db");
            keys = new File(dir, "keys.db");

            if (values.exists() && keys.exists()) {
                readStorage();
            }
        } else {
            throw new RuntimeException("Process collision :(");
        }

    }

    private void writeStorage() throws IOException {
        try (DataOutputStream keysOutput = new DataOutputStream(new FileOutputStream(keys));
        DataOutputStream valuesOutput = new DataOutputStream(new FileOutputStream(values))) {
            keysOutput.writeUTF(KEYS_VALIDATION);
            valuesOutput.writeUTF(VALUES_VALIDATION);
            keysOutput.writeInt(map.size());
            for (map.Entry<K, V> entry : map.entrySet()) {
                keySerialization.writeSerialize(entry.getKey(), keysOutput);
                valueSerialization.writeSerialize(entry.getValue(), valuesOutput);
            }
            keysOutput.close();
            valuesOutput.close();
        }
        catch (IOException exc) {
            throw new RuntimeException("Illegal output operations!");
        }
    }

    private void readStorage() {
        try (DataInputStream valuesInput = new DataInputStream(new FileInputStream(values));
             DataInputStream keysInput = new DataInputStream(new FileInputStream(keys))) {
            String keyValidate = keysInput.readUTF();
            String valueValidate = valuesInput.readUTF();
            if (!VALUES_VALIDATION.equals(valueValidate) || !KEYS_VALIDATION.equals(keyValidate)) {
                throw new RuntimeException("It's not MyKeyValueStorage!");
            }
            int size = keysInput.readInt();
            for (int i = 0; i < size; i++) {
                K key = keySerialization.readSerialize(keysInput);
                V value = valueSerialization.readSerialize(valuesInput);
                Map.put(key, value);
            }
        } catch (IOException exc) {
            throw new RuntimeException("Illegal input operations!");
        }
    }


    @Override
    public V read(K key) {
        isClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return (map.containsKey(key));
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        isClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        isClosed();
        procAccess.delete();
        writeStorage();
        closedAccess = true;
    }
}