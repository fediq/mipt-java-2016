package ru.mipt.java2016.homework.g594.petrov.task3;

/**
 * Created by philipp on 14.11.16.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.*;

class MegaKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int CACHE_SIZE = 0;
    private static final int MEM_TREE_SIZE = 100;
    private final String directory;
    private final String assistDirectory;
    private final String pathToDir;
    private final InterfaceSerialization<K> keySerialization;
    private final InterfaceSerialization<V> valueSerialization;
    private final String typeOfData;

    private boolean isOpen;
    private LinkedHashMap<K, V> cacheMap;
    private HashMap<K, Long> assistKeyOffsetMap;
    private HashMap<K, V> currentTree;
    private HashSet<K> existedKeys;
    private RandomAccessFile assistStorage;

    MegaKeyValueStorage(String path, String dataType, InterfaceSerialization<K> keySerialization,
                        InterfaceSerialization<V> valueSerialization) throws IllegalStateException {
        isOpen = true;
        directory = path + File.separator + "storage.db";
        assistDirectory = path + File.separator + "storage_tmp.db";
        pathToDir = path;
        cacheMap = new LinkedHashMap<>();
        typeOfData = dataType;
        currentTree = new HashMap<>();
        existedKeys = new HashSet<>();
        assistKeyOffsetMap = new HashMap<>();
        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;
        File storage = new File(directory);
        File tmpStorage = new File(assistDirectory);
        if (!storage.exists()) {
            try {
                if (!storage.createNewFile()) {
                    throw new IllegalStateException("Cant create file");
                }
                if (!tmpStorage.createNewFile()) {
                    throw new IllegalStateException("Cant create file");
                }
                assistStorage = new RandomAccessFile(tmpStorage, "rw");
            } catch (IOException | SecurityException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        } else {
            try (DataInputStream keyOffsetReader = new DataInputStream(new
                    BufferedInputStream(new FileInputStream(storage)))) {
                assistStorage = new RandomAccessFile(tmpStorage, "rw");
                String check = keyOffsetReader.readUTF();
                if (!check.equals(typeOfData)) {
                    throw new IllegalStateException("Invalid storage format");
                }
                int number = keyOffsetReader.readInt();
                InterfaceSerialization<Long> offsetReader = new SerializeLong();
                for (int i = 0; i < number; ++i) {
                    K key = this.keySerialization.readValue(keyOffsetReader);
                    Long offset = offsetReader.readValue(keyOffsetReader);
                    existedKeys.add(key);
                    assistKeyOffsetMap.put(key, offset);
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

    private void checkCurrentTree(boolean f) {
        if (currentTree.size() <= MEM_TREE_SIZE && !f) {
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
        checkCurrentTree(false);
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
        checkCurrentTree(false);
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

    private void clearStorage() {
        String transferName = pathToDir + File.separator + "storageTransfer_tmp";
        File transfer = new File(transferName);
        try {
            if (assistStorage.length() <= 2000000000) {
                return;
            }
            if (!transfer.createNewFile()) {
                throw new IllegalStateException("Cant create file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        try (RandomAccessFile transferWriter = new RandomAccessFile(transfer, "rw")) {
            for (Map.Entry<K, Long> iterator : assistKeyOffsetMap.entrySet()) {
                if (!iterator.getValue().equals((long) -1)) {
                    assistStorage.seek(iterator.getValue());
                    iterator.setValue(transferWriter.getFilePointer());
                    valueSerialization.writeValue(valueSerialization.readValue(assistStorage), transferWriter);
                }
            }
            transferWriter.close();
            assistStorage.close();
            File oldStorage = new File(assistDirectory);
            if (!oldStorage.delete()) {
                throw new IllegalStateException("Cant delete file");
            }
            if (!transfer.renameTo(oldStorage)) {
                throw new IllegalStateException("Cant delete file");
            }
            assistStorage = new RandomAccessFile(transfer, "rw");
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void close() throws IllegalStateException {
        if (!isOpen) {
            throw new IllegalStateException("You can't use KVS when it's closed");
        }
        isOpen = false;
        checkCurrentTree(true);
        clearStorage();
        String storageName = pathToDir + File.separator + "storage1.db";
        File newStorage = new File(storageName);
        try {
            if (!newStorage.createNewFile()) {
                throw new IllegalStateException("Cant create new file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        try (DataOutputStream finishWriter = new DataOutputStream(new
                BufferedOutputStream(new FileOutputStream(newStorage)))) {
            finishWriter.writeUTF(typeOfData);
            finishWriter.writeInt(existedKeys.size());
            InterfaceSerialization<Long> offsetWriter = new SerializeLong();
            for (Map.Entry<K, Long> iterator : assistKeyOffsetMap.entrySet()) {
                if (!iterator.getValue().equals((long) -1)) {
                    keySerialization.writeValue(iterator.getKey(), finishWriter);
                    offsetWriter.writeValue(iterator.getValue(), finishWriter);
                }
            }
            finishWriter.close();
            assistStorage.close();
            File oldStorage = new File(directory);
            if (!oldStorage.delete()) {
                throw new IllegalStateException("Cant delete old file");
            }
            if (!newStorage.renameTo(oldStorage)) {
                throw new IllegalStateException("Cant rename file");
            }
        } catch (IOException | SecurityException | IllegalStateException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}


