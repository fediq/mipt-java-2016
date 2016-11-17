package ru.mipt.java2016.homework.g594.petrov.task3;

/**
 * Created by philipp on 14.11.16.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.*;

public class MegaKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final LinkedHashMap<K, V> cacheMap;
    private final String directory;
    private final String pathToDir;
    private final InterfaceSerialization<K> keySerialization;
    private final InterfaceSerialization<V> valueSerialization;
    private final String typeOfData;
    private boolean isOpen;

    private ArrayList<Map.Entry<HashMap<K, Long>, String>> keyOffsetArray;
    private HashMap<K, V> currentTree;
    private static final int CACHE_SIZE = 1200;
    private static final int MEM_TREE_SIZE = 1500;

    public MegaKeyValueStorage(String path, String dataType, InterfaceSerialization<K> keySerialization,
                               InterfaceSerialization<V> valueSerialization) throws IllegalStateException {
        isOpen = true;
        directory = path + File.separator + "storage.db";
        pathToDir = path;
        cacheMap = new LinkedHashMap<>();
        typeOfData = dataType;
        currentTree = new HashMap<>();
        keyOffsetArray = new ArrayList<>();
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        File storage = new File(directory);
        HashMap<K, Long> keyOffsetMap = new HashMap<>();
        if (!storage.exists()) {
            try {
                if (!storage.createNewFile()) {
                    throw new IllegalStateException("Cant create file");
                }
                keyOffsetArray.add(new AbstractMap.SimpleEntry<>(keyOffsetMap, directory));
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
                for (int i = 0; i < number; ++i) {
                    K key = this.keySerialization.readValue(storageInput);
                    Long offset = storageInput.getFilePointer();
                    this.valueSerialization.readValue(storageInput);
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
        for (int i = keyOffsetArray.size() - 1; i >= 0; --i) {
            Map.Entry<HashMap<K, Long>, String> block = keyOffsetArray.get(i);
            if (block.getKey().containsKey(key)) {
                if (block.getKey().get(key).equals((long) -1)) {
                    cacheMap.put(key, null);
                    clearCache();
                    return null;
                } else {
                    V complexValue;
                    try (RandomAccessFile storageInput = new RandomAccessFile(new File(block.getValue()), "r")) {
                        storageInput.seek(block.getKey().get(key));
                        complexValue = valueSerialization.readValue(storageInput);
                        storageInput.close();
                    } catch (IOException | SecurityException | IllegalStateException e) {
                        throw new IllegalStateException(e.getMessage(), e.getCause());
                    }
                    cacheMap.put(key, complexValue);
                    clearCache();
                    return complexValue;
                }
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
            return cacheMap.get(key) != null;
        }
        if (currentTree.containsKey(key)) {
            return currentTree.get(key) != null;
        }
        for (int i = keyOffsetArray.size() - 1; i >= 0; --i) {
            Map.Entry<HashMap<K, Long>, String> block = keyOffsetArray.get(i);
            if (block.getKey().containsKey(key)) {
                return !block.getKey().get(key).equals((long) -1);
            }
        }
        return false;
    }

    private void checkCurrentTree() {
        if (currentTree.size() <= MEM_TREE_SIZE) {
            return;
        }
        String nameOfStorage = pathToDir + File.separator + "tempFile_" + String.valueOf(keyOffsetArray.size());
        File newStorage = new File(nameOfStorage);
        try {
            if (!newStorage.createNewFile()) {
                throw new IllegalStateException("Cant create file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        try (RandomAccessFile writer = new RandomAccessFile(newStorage, "rw")) {
            HashMap<K, Long> newKeyOffsetMap = new HashMap<>();
            for (Map.Entry<K, V> nextEntry : currentTree.entrySet()) {
                if (nextEntry.getValue() != null) {
                    keySerialization.writeValue(nextEntry.getKey(), writer);
                    newKeyOffsetMap.put(nextEntry.getKey(), writer.getFilePointer());
                    valueSerialization.writeValue(nextEntry.getValue(), writer);
                } else {
                    newKeyOffsetMap.put(nextEntry.getKey(), (long) -1);
                }
            }
            keyOffsetArray.add(new AbstractMap.SimpleEntry<>(newKeyOffsetMap, nameOfStorage));
            writer.close();
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
    }

    private HashSet<K> findExistedKeys() {
        HashSet<K> existedElements = new HashSet<>();
        HashSet<K> deletedElements = new HashSet<>();
        for (Map.Entry<K, V> iterator : currentTree.entrySet()) {
            if (iterator.getValue() == null) {
                deletedElements.add(iterator.getKey());
            } else {
                existedElements.add(iterator.getKey());
            }
        }
        for (Map.Entry<HashMap<K, Long>, String> iterator : keyOffsetArray) {
            for (Map.Entry<K, Long> jterator : iterator.getKey().entrySet()) {
                if (!(deletedElements.contains(jterator.getKey()) || existedElements.contains(jterator.getKey()))) {
                    if (jterator.getValue().equals((long) -1)) {
                        deletedElements.add(jterator.getKey());
                    } else {
                        existedElements.add(jterator.getKey());
                    }
                }
            }
        }
        return existedElements;
    }

    @Override
    public int size() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return findExistedKeys().size();
    }

    @Override
    public Iterator<K> readKeys() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        return findExistedKeys().iterator();
    }

    @Override
    public void close() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        isOpen = false;
        HashSet<K> existedKeys = new HashSet<>();
        HashSet<K> deletedKeys = new HashSet<>();
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
            finishWriter.writeInt(findExistedKeys().size());
            for (Map.Entry<K, V> iterator : currentTree.entrySet()) {
                if (iterator.getValue() == null) {
                    deletedKeys.add(iterator.getKey());
                } else {
                    existedKeys.add(iterator.getKey());
                    keySerialization.writeValue(iterator.getKey(), finishWriter);
                    valueSerialization.writeValue(iterator.getValue(), finishWriter);
                }
            }
            for (Map.Entry<HashMap<K, Long>, String> iterator : keyOffsetArray) {
                try (RandomAccessFile dataReader = new RandomAccessFile(new File(iterator.getValue()), "r")) {
                    for (Map.Entry<K, Long> jterator : iterator.getKey().entrySet()) {
                        if (!(deletedKeys.contains(jterator.getKey()) || existedKeys.contains(jterator.getKey()))) {
                            if (jterator.getValue().equals((long) -1)) {
                                deletedKeys.add(jterator.getKey());
                            } else {
                                existedKeys.add(jterator.getKey());
                                keySerialization.writeValue(jterator.getKey(), finishWriter);
                                dataReader.seek(jterator.getValue());
                                valueSerialization.writeValue(valueSerialization.readValue(dataReader), finishWriter);
                            }
                        }
                    }
                    dataReader.close();
                } catch (IOException | SecurityException | IllegalStateException e) {
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                }
            }
            finishWriter.close();
            File oldStorage = new File(directory);
            if (!oldStorage.delete()) {
                throw new IllegalStateException("Cant delete old directory");
            }
            if (!newStorage.renameTo(oldStorage)) {
                throw new IllegalStateException("Cant rename file");
            }
        } catch (IOException | SecurityException | IllegalStateException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}


