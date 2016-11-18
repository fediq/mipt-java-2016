package ru.mipt.java2016.homework.g594.kozlov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.FileWorker;
import ru.mipt.java2016.homework.g594.kozlov.task2.StorageException;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Anatoly on 18.11.2016.
 */
public class FastStorage<K, V> implements KeyValueStorage<K, V> {

    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final Map<K, Long> keyMap = new HashMap<>();
    private final Set<K> deleteSet = new HashSet<>();
    private final String dirPath;
    private final FileWorker indexFile;
    private final FileWorker tabFile;
    private final FileWorker deleteFile;
    private Long currOffset = new Long(0);
    private boolean writing = true;
    private boolean isClosedFlag = false;

    public FastStorage(SerializerInterface<K> keySerializer, SerializerInterface<V> valueSerializer, String dirPath) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        if (dirPath == null || dirPath.equals("")) {
            this.dirPath = "";
        } else {
            this.dirPath = dirPath + File.separator;
        }
        indexFile = new FileWorker(this.dirPath + "indexfile.db");
        tabFile = new FileWorker(this.dirPath + "tabfile.db");
        deleteFile = new FileWorker(this.dirPath + "deletes.db");
        if (!indexFile.exists()) {
            indexFile.createFile();
            tabFile.createFile();
            deleteFile.createFile();
        }
        currOffset = tabFile.fileLen();
        initStorage();
        tabFile.appMode();
    }

    private void initStorage() {
        String nextKey = indexFile.readNextToken();
        while (nextKey != null) {
            Long offset = Long.parseLong(indexFile.readNextToken());
            try {
                keyMap.put(keySerializer.deserialize(nextKey), offset);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = indexFile.readNextToken();
        }
        indexFile.close();
        nextKey = deleteFile.readNextToken();
        while (nextKey != null) {
            try {
                K key = keySerializer.deserialize(nextKey);
                deleteSet.add(key);
                keyMap.remove(key);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = deleteFile.readNextToken();
        }
        indexFile.appMode();
        deleteFile.close();
    }

    private void isClosed() {
        if (isClosedFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        isClosed();
        Long offset = keyMap.get(key);
        if (offset != null) {
            return loadKey(offset);
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return keyMap.get(key) != null;
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        deleteSet.remove(key);
        keyMap.put(key, currOffset);
        flush(key, value);
    }

    @Override
    public void delete(K key) {
        isClosed();
        deleteSet.add(key);
        keyMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return keyMap.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return keyMap.size();
    }

    @Override
    public void close() throws IOException {
        isClosedFlag = true;
        flushDeletes();
        deleteFile.close();
        indexFile.close();
        tabFile.close();
    }

    private V loadKey(long offset) {
        if (writing) {
            tabFile.close();
            writing = false;
        }
        tabFile.moveToOffset(offset);
        try {
            return valueSerializer.deserialize(tabFile.readNextToken());
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    private void flush(K key, V value) {
        if (!writing) {
            tabFile.close();
            tabFile.appMode();
            writing = true;
        }
        indexFile.bufferedWrite(keySerializer.serialize(key));
        indexFile.bufferedWrite(currOffset.toString());
        currOffset += tabFile.bufferedWrite(valueSerializer.serialize(value));
    }

    private void flushDeletes() {
        if (!deleteFile.exists()) {
            deleteFile.createFile();
        }
        for (K entry : deleteSet) {
            deleteFile.bufferedWrite(keySerializer.serialize(entry));
        }
        deleteFile.bufferedWriteSubmit();
        deleteSet.clear();
    }
}
