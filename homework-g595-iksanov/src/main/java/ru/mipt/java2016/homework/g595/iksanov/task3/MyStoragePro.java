package ru.mipt.java2016.homework.g595.iksanov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Эмиль
 */
public class MyStoragePro<K, V> implements KeyValueStorage<K, V> {
    private static final long BUFFER_SIZE = (long) (1000);
    private final String storageName = "myStorage";
    private final String mapName = "myMap";
    private NewSerializationStrategy<K> keySerializationStrategy;
    private NewSerializationStrategy<V> valueSerializationStrategy;
    private NewSerializationStrategy<Long> offsetSerializationStrategy = NewStrategyForLong.getInstance();
    private Map<K, V> buffer = new HashMap<>();
    private Map<K, Long> offsets = new HashMap<>();
    private boolean isClosed;
    private RandomAccessFile storage;
    private int numOfRemovals;
    private K cacheKey;
    private V cacheValue;
    private boolean cacheUsed = false;
    private String path;

    public MyStoragePro(String pathArgument, NewSerializationStrategy<K> keySerializationStrategyArgument,
                     NewSerializationStrategy<V> valueSerializationStrategyArgument) {
        keySerializationStrategy = keySerializationStrategyArgument;
        valueSerializationStrategy = valueSerializationStrategyArgument;
        path = pathArgument;
        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += File.separator +  "file";
        }
        File storageFile = new File(path + storageName);
        File mapFile = new File(path + mapName);
        try {
            storage = new RandomAccessFile(storageFile, "rw");
        } catch (IOException e) {
            throw new RuntimeException("Reading from file problem");
        }
        if (storageFile.exists() && mapFile.exists()) {
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = mapTempFile.readInt();
                numOfRemovals = mapTempFile.readInt();
                for (int i = 0; i < n; i++) {
                    K key = keySerializationStrategy.read(mapTempFile);
                    Long offset = offsetSerializationStrategy.read(mapTempFile);
                    offsets.put(key, offset);
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading from file problem");
            }
        }
    }

    @Override
    public V read(K key) {
        V result;
        checkNotClosed();
        if (cacheUsed && cacheKey == key) {
            return cacheValue;
        }
        Long offset = offsets.get(key);
        if (offset == null) {
            return null;
        } else if (offset == -1) {
            result = buffer.get(key);
            cacheUsed = true;
            cacheKey = key;
            cacheValue = result;
        } else {
            try {
                storage.seek(offset);
                result = valueSerializationStrategy.read(storage);
                cacheUsed = true;
                cacheKey = key;
                cacheValue = result;
            } catch (IOException e) {
                throw new RuntimeException("Reading from file problem");
            }
        }
        return result;
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return offsets.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();

        V resultFromBuffer = buffer.put(key, value);
        Long resultFromOffsets = offsets.put(key, (long) (-1));
        if (resultFromBuffer == null && resultFromOffsets != null) {
            numOfRemovals++;
        }

        cacheUsed = true;
        cacheKey = key;
        cacheValue = value;
        if (buffer.size() >= BUFFER_SIZE) {
            dump();
        }
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        V resultFromBuffer = buffer.remove(key);
        Long resultFromOffsets = offsets.remove(key);
        if (resultFromBuffer == null && resultFromOffsets != null) {
            numOfRemovals++;
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkNotClosed();

        return offsets.size();
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Closed File");
        }
    }

    private void dump() {
        try {
            storage.seek((long) (storage.length()));
            for (Map.Entry<K, V> entry : buffer.entrySet()) {
                Long offset = valueSerializationStrategy.write(entry.getValue(), storage);
                keySerializationStrategy.write(entry.getKey(), storage);
                offsets.put(entry.getKey(), offset);
            }
            buffer.clear();
        } catch (IOException e) {
            throw new RuntimeException("Reading from file problem");
        }
    }

    private void dumpToNewFile(RandomAccessFile newStorage) {
        try {
            newStorage.seek((long) (-1));
            for (Map.Entry<K, V> entry : buffer.entrySet()) {
                Long offset = valueSerializationStrategy.write(entry.getValue(), newStorage);
                keySerializationStrategy.write(entry.getKey(), newStorage);
                offsets.put(entry.getKey(), offset);
            }
            buffer.clear();
        } catch (IOException e) {
            throw new RuntimeException("Reading from file problem");
        }
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        dump();
        if (numOfRemovals >= 10000) {
            Map<K, Long> newOffsets = new HashMap<>();
            File newStorageFile = new File(path + storageName + "new");
            RandomAccessFile newStorage = new RandomAccessFile(newStorageFile, "rw");
            storage.seek(0);
            Long offset = (long) 0;
            Long endOffset = storage.length();
            V value;
            K key;
            while (offset != endOffset) {
                value = valueSerializationStrategy.read(storage);
                key = keySerializationStrategy.read(storage);
                if (offsets.get(key) == offset) {
                    buffer.put(key, value);
                    if (buffer.size() >= BUFFER_SIZE) {
                        dumpToNewFile(newStorage);
                    }
                }
                offset = storage.getFilePointer();
            }
            dumpToNewFile(newStorage);

            storage.close();
            newStorage.close();
            File storageFile = new File(path + storageName);
            storageFile.delete();
            storageFile = new File(path + storageName);
            newStorageFile.renameTo(storageFile);
            numOfRemovals = 0;
            File mapFile = new File(path + mapName);
            mapFile.delete();
            mapFile = new File(path + mapName);
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = newOffsets.size();
                mapTempFile.writeInt(n);
                mapTempFile.writeInt(numOfRemovals);
                for (Map.Entry<K, Long> entry : newOffsets.entrySet()) {
                    keySerializationStrategy.write(entry.getKey(), mapTempFile);
                    offsetSerializationStrategy.write(entry.getValue(), mapTempFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading from file problem");
            }
        } else {
            File mapFile = new File(path + mapName);
            mapFile.delete();
            mapFile = new File(path + mapName);
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = offsets.size();
                mapTempFile.writeInt(n);
                mapTempFile.writeInt(numOfRemovals);
                for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                    keySerializationStrategy.write(entry.getKey(), mapTempFile);
                    offsetSerializationStrategy.write(entry.getValue(), mapTempFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading from file problem");
            }
            storage.close();
        }
        isClosed = true;
        offsets.clear();
    }
}
