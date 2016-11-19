package ru.mipt.java2016.homework.g594.petrov.task3;

/**
 * Created by philipp on 14.11.16.
 */

import com.sun.org.apache.bcel.internal.generic.ILOAD;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.*;

class MegaKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int CACHE_SIZE = 0;
    private static final int MEM_TREE_SIZE = 1300;
    private final String directory;
    private final String assistDirectory;
    private final String pathToDir;
    private final InterfaceSerialization<K> keySerialization;
    private final InterfaceSerialization<V> valueSerialization;
    private final String typeOfData;

    private boolean isOpen;
    private LinkedHashMap<K, V> cacheMap;
    private HashMap<K, Long> mainKeyOffsetMap;
    private HashMap<K, Long> assistKeyOffsetMap;
    private HashMap<K, V> currentTree;
    private HashSet<K> existedKeys;
    private RandomAccessFile mainStorage;
    private RandomAccessFile assistStorage;

    MegaKeyValueStorage(String path, String dataType, InterfaceSerialization<K> keySerialization,
                        InterfaceSerialization<V> valueSerialization) throws IllegalStateException {
        isOpen = true;
        directory = path + File.separator + "storage.db";
        assistDirectory= path + File.separator + "storage_tmp.db";
        pathToDir = path;
        cacheMap = new LinkedHashMap<>();
        typeOfData = dataType;
        currentTree = new HashMap<>();
        existedKeys = new HashSet<>();
        mainKeyOffsetMap = new HashMap<>();
        assistKeyOffsetMap = new HashMap<>();
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        File storage = new File(directory);
        File tmpStorage = new File(assistDirectory);
        try {
            if (!tmpStorage.createNewFile()) {
                throw new IllegalStateException("Cant create new file");
            }
            assistStorage = new RandomAccessFile(tmpStorage, "rw");
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        if (!storage.exists()) {
            try {
                if (!storage.createNewFile()) {
                    throw new IllegalStateException("Cant create file");
                }
                mainStorage = new RandomAccessFile(storage, "rw");
            } catch (IOException | SecurityException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        } else {
            try {
                mainStorage = new RandomAccessFile(storage, "rw");
                String check = mainStorage.readUTF();
                if (!check.equals(typeOfData)) {
                    throw new IllegalStateException("Invalid storage format");
                }
                int number = mainStorage.readInt();
                for (int i = 0; i < number; ++i) {
                    K key = this.keySerialization.readValue(mainStorage);
                    Long offset = mainStorage.getFilePointer();
                    this.valueSerialization.readValue(mainStorage);
                    existedKeys.add(key);
                    mainKeyOffsetMap.put(key, offset);
                }
            } catch (IllegalStateException | IOException e) {
                throw new IllegalStateException("Invalid storage format", e.getCause());
            }

        }
    }

    private void clearCache() {
        if (cacheMap.size() > CACHE_SIZE) {
            Iterator<Map.Entry<K, V>> i = cacheMap.entrySet().iterator();
            i.next();
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

        if (assistKeyOffsetMap.containsKey(key)) {
            if (assistKeyOffsetMap.get(key).equals((long) -1)) {
                cacheMap.put(key, null);
                clearCache();
                return null;
            }
            V complexValue;
            try {
                assistStorage.seek(assistKeyOffsetMap.get(key));
                complexValue = valueSerialization.readValue(assistStorage);
                cacheMap.put(key, complexValue);
                clearCache();
                return complexValue;
            } catch (IOException | IllegalStateException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        }

        if (mainKeyOffsetMap.containsKey(key)) {
            if (mainKeyOffsetMap.get(key).equals((long) -1)) {
                cacheMap.put(key, null);
                clearCache();
                return null;
            }
            V complexValue;
            try {
                mainStorage.seek(mainKeyOffsetMap.get(key));
                complexValue = valueSerialization.readValue(mainStorage);
                cacheMap.put(key, complexValue);
                clearCache();
                return complexValue;
            } catch (IOException | IllegalStateException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
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
        return existedKeys.contains(key);
    }

    private void checkCurrentTree() {
        if (currentTree.size() <= MEM_TREE_SIZE) {
            return;
        }
        try {
            assistStorage.seek(assistStorage.length());
            for (Map.Entry<K, V> nextEntry : currentTree.entrySet()) {
                if (nextEntry.getValue() != null) {
                    assistKeyOffsetMap.put(nextEntry.getKey(), assistStorage.getFilePointer());
                    valueSerialization.writeValue(nextEntry.getValue(), assistStorage);
                } else {
                    assistKeyOffsetMap.put(nextEntry.getKey(), (long) -1);
                }
            }
        } catch (IOException | SecurityException | IllegalStateException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        currentTree.clear();
    }

    @Override
    public void write(K key, V value) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
        }
        currentTree.put(key, value);
        checkCurrentTree();
        existedKeys.add(key);
    }

    @Override
    public void delete(K key) throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, null);
        }
        currentTree.put(key, null);
        checkCurrentTree();
        existedKeys.remove(key);
    }

    @Override
    public int size() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return existedKeys.size();
    }

    @Override
    public Iterator<K> readKeys() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return existedKeys.iterator();
    }

    @Override
    public void close() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        isOpen = false;
        String storageName = pathToDir + File.separator + "storage1.db";
        File newStorage = new File(storageName);
        try {
            if (!newStorage.createNewFile()) {
                throw new IllegalStateException("Cant create new file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        try (RandomAccessFile finishWriter = new RandomAccessFile(newStorage, "rw")) {
            finishWriter.writeUTF(typeOfData);
            finishWriter.writeInt(existedKeys.size());
            for (Map.Entry<K, V> iterator : currentTree.entrySet()) {
                if (iterator.getValue() != null) {
                    keySerialization.writeValue(iterator.getKey(), finishWriter);
                    valueSerialization.writeValue(iterator.getValue(), finishWriter);
                }
            }
            for (Map.Entry<K, Long> iterator : assistKeyOffsetMap.entrySet()) {
                if (!(currentTree.containsKey(iterator.getKey()))) {
                    if (!iterator.getValue().equals((long) -1)) {
                        keySerialization.writeValue(iterator.getKey(), finishWriter);
                        assistStorage.seek(iterator.getValue());
                        valueSerialization.writeValue(valueSerialization.readValue(assistStorage), finishWriter);
                    }
                }
            }
            for (Map.Entry<K, Long> iterator : mainKeyOffsetMap.entrySet()) {
                if (!(currentTree.containsKey(iterator.getKey()) ||
                        assistKeyOffsetMap.containsKey(iterator.getKey()))) {
                    if (!iterator.getValue().equals((long) -1)) {
                        keySerialization.writeValue(iterator.getKey(), finishWriter);
                        mainStorage.seek(iterator.getValue());
                        valueSerialization.writeValue(valueSerialization.readValue(mainStorage), finishWriter);
                    }
                }
            }
            finishWriter.close();
            assistStorage.close();
            mainStorage.close();
            File oldStorage = new File(directory);
            File tmpStorage = new File(assistDirectory);
            if (!oldStorage.delete()) {
                throw new IllegalStateException("Cant delete old file");
            }
            if (!tmpStorage.delete()) {
                throw new IllegalStateException("CAnt delete old file");
            }
            if (!newStorage.renameTo(oldStorage)) {
                throw new IllegalStateException("Cant rename file");
            }
        } catch (IOException | SecurityException | IllegalStateException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}


