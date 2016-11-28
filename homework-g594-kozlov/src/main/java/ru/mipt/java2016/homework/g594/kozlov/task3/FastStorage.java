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
    private final Map<K, Long> keyMap;
    private final Set<K> deleteSet = new HashSet<>();
    private final String dirPath;
    private final FileWorker indexFile;
    private final FileWorker tabFile;
    private final FileWorker deleteFile;
    private final FileWorker validFile;
    private Long currOffset = new Long(0);
    private boolean writing = true;
    private boolean isClosedFlag = false;
    private final Integer lock = 42;

    public FastStorage(SerializerInterface<K> keySerializer, SerializerInterface<V> valueSerializer, String dirPath) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        keyMap = Collections.synchronizedMap(new HashMap<>());
        if (dirPath == null || dirPath.equals("")) {
            this.dirPath = "";
        } else {
            this.dirPath = dirPath + File.separator;
        }
        indexFile = new FileWorker(this.dirPath + "indexfile.db", true);
        tabFile = new FileWorker(this.dirPath + "tabfile.db", false);
        deleteFile = new FileWorker(this.dirPath + "deletes.db", false);
        validFile = new FileWorker(this.dirPath + "validfile.db", false);
        if (!indexFile.exists()) {
            indexFile.createFile();
            tabFile.createFile();
            deleteFile.createFile();
            validFile.createFile();
        } else {
            currOffset = tabFile.fileLen();
            initStorage();
        }
        tabFile.appMode();
    }

    private void initStorage() {
        indexFile.close();
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
        long checksum = indexFile.getCheckSum();
        if (checksum != Long.parseLong(validFile.readNextToken())) {
            throw new RuntimeException("validation error");
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
        synchronized (lock) {
            isClosed();
            Long offset = keyMap.get(key);
            if (offset != null) {
                return loadKey(offset);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (lock) {
            isClosed();
            return keyMap.get(key) != null;
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (lock) {
            isClosed();
            deleteSet.remove(key);
            keyMap.put(key, currOffset);
            flush(key, value);
        }
    }

    @Override
    public void delete(K key) {
        synchronized (lock) {
            isClosed();
            deleteSet.add(key);
            keyMap.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (lock) {
            isClosed();
            return keyMap.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            isClosed();
            return keyMap.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            isClosedFlag = true;
            flushDeletes();
            deleteFile.close();
            indexFile.close();
            tabFile.close();
            writeChecksum();
        }
    }

    private void writeChecksum() {
        validFile.close();
        validFile.bufferedWrite(Long.toString(indexFile.getCheckSum()));
        validFile.bufferedWriteSubmit();
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