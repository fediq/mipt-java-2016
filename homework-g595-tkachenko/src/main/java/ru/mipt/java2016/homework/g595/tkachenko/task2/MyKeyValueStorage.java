package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> Map = new HashMap<>();
    private File values;
    private File keys;
    private static final String tryProc = "Some process is already working :(";
    private static final String validateValues = "This is a Values storage.";
    private static final String validateKeys = "This is a Keys storage.";
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

        procAccess = new File(tryProc);

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
            keysOutput.writeUTF(validateKeys);
            valuesOutput.writeUTF(validateValues);
            keysOutput.writeInt(Map.size());
            for (Map.Entry<K, V> entry : Map.entrySet()) {
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
            if (!validateValues.equals(valueValidate) || !validateKeys.equals(keyValidate)) {
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
        return Map.get(key);
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return (Map.containsKey(key));
    };

    @Override
    public void write(K key, V value) {
        isClosed();
        Map.put(key, value);
    };

    @Override
    public void delete(K key) {
        isClosed();
        Map.remove(key);
    };

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return Map.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return Map.size();
    }

    @Override
    public void close() throws IOException {
        isClosed();
        procAccess.delete();
        writeStorage();
        closedAccess = true;
    }
}