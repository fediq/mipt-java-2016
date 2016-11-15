package ru.mipt.java2016.homework.g594.petrov.task3;

/**
 * Created by philipp on 14.11.16.
 */


import java.io.DataInputStream;
import java.io.FileInputStream;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by philipp on 30.10.16.
 */

public class MegaKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    public MegaKeyValueStorage(String path, String dataType, InterfaceSerialization<K> keySerialization,
                               InterfaceSerialization<V> valueSerialization) {
        isOpen = true;
        directory = path + "/storage.db";
        keyValueStorage = new HashMap<>();
        typeOfData = dataType;
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        File storage = new File(directory);
        if (!storage.exists()) {
            try {
                storage.createNewFile();
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        } else {
            try (DataInputStream storageInput = new DataInputStream(new FileInputStream(storage))) {
                String check = storageInput.readUTF();
                if (!check.equals(typeOfData)) {
                    throw new IllegalStateException("Invalid storage format");
                }
                int number = storageInput.readInt();
                for (int i = 0; i < number; ++i) {
                    K key = this.keySerialization.readValue(storageInput);
                    V value = this.valueSerialization.readValue(storageInput);
                    keyValueStorage.put(key, value);
                }
                storageInput.close();
            } catch (Exception e) {
                throw new IllegalStateException("Invalid storage format", e.getCause());
            }

        }
    }

    @Override
    public V read(K key) {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.get(key);
    }

    @Override
    public boolean exists(K key) {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        keyValueStorage.put(key, value);
    }

    @Override
    public void delete(K key) {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        keyValueStorage.remove(key);
    }

    @Override
    public int size() {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.size();
    }

    @Override
    public Iterator<K> readKeys() {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.keySet().iterator();
    }

    @Override
    public void close() throws IOException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        try (DataOutputStream outputStorage = new DataOutputStream(new FileOutputStream(directory))) {
            isOpen = false;
            outputStorage.writeUTF(typeOfData);
            outputStorage.writeInt(keyValueStorage.size());
            for (HashMap.Entry<K, V> iterator : keyValueStorage.entrySet()) {
                keySerialization.writeValue(iterator.getKey(), outputStorage);
                valueSerialization.writeValue(iterator.getValue(), outputStorage);
            }
            outputStorage.close();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }


    private final HashMap<K, V> keyValueStorage;
    private final String directory;
    private final InterfaceSerialization<K> keySerialization;
    private final InterfaceSerialization<V> valueSerialization;
    private final String typeOfData;
    private boolean isOpen;
}

