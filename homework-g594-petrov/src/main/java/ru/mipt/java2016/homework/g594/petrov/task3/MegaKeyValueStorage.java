package ru.mipt.java2016.homework.g594.petrov.task3;

/**
 * Created by philipp on 14.11.16.
 */


import java.io.DataInputStream;
import java.io.FileInputStream;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.*;

/**
 * Created by philipp on 30.10.16.
 */

public class MegaKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final LinkedHashMap<K, V> cacheMap;
    private final String directory;
    private final InterfaceSerialization<K> keySerialization;
    private final InterfaceSerialization<V> valueSerialization;
    private final String typeOfData;
    private boolean isOpen;

    private ArrayList<Map.Entry<HashMap<K, Long>, String>> keyOffsetArray;
    private TreeMap<K, V> currentTree;
    private final int CACHE_SIZE = 1000;

    public MegaKeyValueStorage(String path, String dataType, InterfaceSerialization<K> keySerialization,
                               InterfaceSerialization<V> valueSerialization) throws IllegalStateException {
        isOpen = true;
        directory = path + File.separator + "storage.db";
        cacheMap = new LinkedHashMap<K, V>();
        typeOfData = dataType;
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        File storage = new File(directory);
        if (!storage.exists()) {
            try {
                storage.createNewFile();
            } catch (IOException | SecurityException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        } else {
            try (RandomAccessFile storageInput = new RandomAccessFile(storage, "r")) {
                String check = storageInput.readUTF();
                if (!check.equals(typeOfData)) {
                    throw new IllegalStateException("Invalid storage format");
                }
                int number = storageInput.readInt();
                HashMap<K, Long> keyOffsetMap = new HashMap<K, Long>();
                for (int i = 0; i < number; ++i) {
                    K key = this.keySerialization.readValue(storageInput);
                    Long offset = storageInput.getFilePointer();
                    V value = this.valueSerialization.readValue(storageInput);
                    keyOffsetMap.put(key, offset);
                }
                keyOffsetArray.add(new AbstractMap.SimpleEntry<>(keyOffsetMap, directory));
                storageInput.close();
            } catch (IllegalStateException | IOException e) {
                throw new IllegalStateException("Invalid storage format", e.getCause());
            }

        }
    }

    private void clearCache() {
        if (cacheMap.size() > CACHE_SIZE) {
            Iterator<Map.Entry<K, V>> i = cacheMap.entrySet().iterator();
            i.remove();
        }
    }

    @Override
    public V read(K key) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        if (cacheMap.containsKey(key)) {
            V cacheValue = cacheMap.remove(key);
            cacheMap.put(key, cacheValue);
            return cacheValue;
        }

        if (currentTree.containsKey(key)) {
            V currentValue = currentTree.get(key);
            cacheMap.put(key, currentValue);
            clearCache();
            return currentValue;
        }
        for (Map.Entry<HashMap<K, Long>, String> iterator : keyOffsetArray) {
            if (iterator.getKey().containsKey(key)) {
                V complexValue;
                try (RandomAccessFile storageInput = new RandomAccessFile(new File(iterator.getValue()), "r")) {
                    storageInput.seek(iterator.getKey().get(key));
                    complexValue = valueSerialization.readValue(storageInput);
                    storageInput.close();
                } catch (IOException | SecurityException e) {
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                }
                cacheMap.put(key, complexValue);
                clearCache();
                return complexValue;
            }
        }
        cacheMap.put(key, null);
        clearCache();
        return null;
    }

    @Override
    public boolean exists(K key) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        if (cacheMap.containsKey(key)) {
            return true;
        }
        if (currentTree.containsKey(key)) {
            return true;
        }
        for (Map.Entry<HashMap<K, Long>, String> iterator : keyOffsetArray) {
            if (iterator.getKey().containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(K key, V value) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        keyValueStorage.put(key, value);
    }

    @Override
    public void delete(K key) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        keyValueStorage.remove(key);
    }

    @Override
    public int size() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.size();
    }

    @Override
    public Iterator<K> readKeys() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return keyValueStorage.keySet().iterator();
    }

    @Override
    public void close() throws IllegalStateException {
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

}


