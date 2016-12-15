package ru.mipt.java2016.homework.g594.kozlov.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.FileWorker;
import ru.mipt.java2016.homework.g594.kozlov.task2.StorageException;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private final FileWorker lockFile;
    private Long currOffset = new Long(0);
    private boolean writing = true;
    private boolean isClosedFlag = false;
    private final Integer lock = 42;
    private boolean needRebuild = false;
    private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws StorageException {
                            V result = loadKey(keyMap.get(k));
                            if (result == null) {
                                throw new StorageException("no key");
                            }
                            return result;
                        }
                    });

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
        lockFile = new FileWorker(this.dirPath + "lockfile.db", false);
        if (lockFile.exists()) {
            throw new RuntimeException("there is working storage");
        } else {
            lockFile.createFile();
        }
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
        int cnt = 0;
        String nextKey = indexFile.readNextToken();
        while (nextKey != null) {
            Long offset = Long.parseLong(indexFile.readNextToken());
            try {
                keyMap.put(keySerializer.deserialize(nextKey), offset);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = indexFile.readNextToken();
            cnt++;
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
            cnt++;
        }
        long checksum = indexFile.getCheckSum();
        if (checksum != Long.parseLong(validFile.readNextToken())) {
            throw new RuntimeException("validation error");
        }
        indexFile.appMode();
        deleteFile.close();
        if (cnt > 2 * keyMap.size()) {
            needRebuild = true;
        }
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
                try {
                    return cacheValues.get(key);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
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
            if (!isClosedFlag) {
                isClosedFlag = true;
                if (needRebuild) {
                    rebuild();
                } else {
                    flushDeletes();
                    deleteFile.close();
                    indexFile.close();
                    tabFile.close();
                    writeChecksum();
                }
                lockFile.delete();
            }
        }
    }

    private void rebuild() {
        FileWorker newVal = new FileWorker(dirPath + "nva.db", false);
        newVal.createFile();
        deleteFile.close();
        deleteFile.delete();
        deleteFile.createFile();
        validFile.close();
        validFile.delete();
        validFile.createFile();
        indexFile.close();
        indexFile.delete();
        indexFile.createFile();
        currOffset = 0L;
        for (Map.Entry<K, Long> entry: keyMap.entrySet()) {
            indexFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
            indexFile.bufferedWrite(currOffset.toString());
            currOffset += newVal.bufferedWrite(valueSerializer.serialize(loadKey(entry.getValue())));
        }
        indexFile.bufferedWriteSubmit();
        newVal.bufferedWriteSubmit();
        writeChecksum();
        tabFile.close();
        tabFile.delete();
        newVal.rename(dirPath + "tabfile.db");
        newVal.close();
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