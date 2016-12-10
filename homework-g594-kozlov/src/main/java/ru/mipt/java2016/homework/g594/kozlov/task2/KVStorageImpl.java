package ru.mipt.java2016.homework.g594.kozlov.task2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class KVStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private static final int CACHE_SIZE = 1000;

    private int nextFileNum = 1;

    private final FileWorker configFile;

    private FileNames[] workFileNames = null;

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private final String path;

    private final Map<K, ValueWrapper> storageChanges = new TreeMap<K, ValueWrapper>();

    private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE * 5)
            .build(
                    new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws StorageException {
                            V result = loadKey(k);
                            if (result == null) {
                                throw new StorageException("no key");
                            }
                            return result;
                        }
                    });

    private Set<K> iterKeySet = null;
    private static final String VALIDATE_STRING = "itismyawesomestoragedontfakeit";

    private Boolean isClosedFlag = false;

    private class ValueWrapper {
        ValueWrapper(boolean st, V obj) {
            deleted = st;
            object = obj;
        }

        private boolean deleted = false;
        private V object = null;
    }

    public KVStorageImpl(String dirPath, SerializerInterface<K> keySerializer,
                         SerializerInterface<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        if (dirPath.length() > 0) {
            path = dirPath + File.separator;
        } else {
            path = "";
        }
        configFile = new FileWorker(path + "mydbconfig.db");

        try {
            if (configFile.exists()) {
                if (!initKeySet()) {
                    throw new RuntimeException("Invalid File");
                }
            }
        } catch (FileNotFoundException except) {
            configFile.createFile();
            writeSysInfo();
            iterKeySet = new TreeSet<K>();
        }
    }

    private static class FileNames implements Comparable<FileNames> {
        FileNames(String str, long time) {
            fileName = str;
            timest = time;
        }

        private String fileName;
        private Long timest;

        private String getFileName() {
            return fileName;
        }

        public Long getTimestamp() {
            return timest;
        }

        @Override
        public int compareTo(FileNames other) {
            return Long.compare(timest, other.timest);
        }
    }

    private boolean validateFile() throws FileNotFoundException {
        configFile.exists();
        configFile.refresh();
        String token = configFile.readNextToken();
        if (token == null || !token.equals(VALIDATE_STRING)) {
            return false;
        }
        token = configFile.readNextToken();
        if (token == null || !token.equals(keySerializer.getClassString())) {
            return false;
        }
        token = configFile.readNextToken();
        if (token == null || !token.equals(valueSerializer.getClassString())) {
            return false;
        }
        Vector<FileNames> vect = new Vector<>();
        token = configFile.readNextToken();
        while (token != null) {
            String[] tokens = token.split(" ");
            vect.add(new FileNames(tokens[0], Long.parseLong(tokens[1])));
            int fileNum = Integer.parseInt(tokens[0].substring(8, tokens[0].length()));
            if (fileNum >= nextFileNum) {
                nextFileNum = fileNum + 1;
            }
            token = configFile.readNextToken();
        }
        workFileNames = new FileNames[vect.size()];
        vect.toArray(workFileNames);
        Arrays.sort(workFileNames);
        return true;
    }

    private void isClosed() {
        if (isClosedFlag) {
            throw new RuntimeException("Storage is closed");
        }
    }

    @Override
    public V read(K key) {
        if (exists(key)) {
            ValueWrapper value = storageChanges.get(key);
            if (value != null) {
                return value.object;
            }
            try {
                return cacheValues.get(key);
            } catch (ExecutionException e) {
                throw new RuntimeException("cache error");
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return iterKeySet.contains(key);
    }

    @Override
    public void write(K key, V value) {
        if (!exists(key)) {
            iterKeySet.add(key);
        }
        storageChanges.put(key, new ValueWrapper(false, value));
        cacheValues.put(key, value);
        changesCheck();
    }

    @Override
    public void delete(K key) {
        if (exists(key)) {
            storageChanges.put(key, new ValueWrapper(true, null));
            cacheValues.invalidate(key);
            iterKeySet.remove(key);
            changesCheck();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return iterKeySet.iterator();
    }

    @Override
    public int size() {
        isClosed();
        return iterKeySet.size();
    }

    @Override
    public void close() {
        iterKeySet.clear();
        isClosedFlag = true;
        flushTemp();
        configFile.refresh();
    }

    private void changesCheck() {
        if (storageChanges.size() >= CACHE_SIZE) {
            flushTemp();
            storageChanges.clear();
        }
    }

    private boolean initKeySet() {
        try {
            iterKeySet = new TreeSet<K>();
            if (validateFile()) {
                for (FileNames name: workFileNames) {
                    Map<K, Long> map = loadKeysFrom(path + name.fileName);
                    for (Map.Entry<K, Long> entry: map.entrySet()) {
                        if (entry.getValue() == -1) {
                            iterKeySet.remove(entry.getKey());
                        } else {
                            iterKeySet.add(entry.getKey());
                        }
                    }
                }
                for (Map.Entry<K, ValueWrapper> entry: storageChanges.entrySet()) {
                    if (entry.getValue().deleted) {
                        iterKeySet.remove(entry.getKey());
                    } else {
                        iterKeySet.add(entry.getKey());
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<K, Long> loadKeysFrom(String fileName) {
        FileWorker file = new FileWorker(fileName + ".ind");
        Map<K, Long> map = new TreeMap<K, Long>();
        String nextKey = file.readNextToken();
        while (nextKey != null) {
            Long offset = file.readLong();
            try {
                map.put(keySerializer.deserialize(nextKey), offset);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = file.readNextToken();
        }
        return map;
    }

    private V loadKey(K key) {
        try {
            if (validateFile()) {
                for (int i = workFileNames.length - 1; i >= 0; --i) {
                    FileNames name = workFileNames[i];
                    Map<K, Long> map = loadKeysFrom(path + name.fileName);
                    Long offset = map.get(key);
                    if (offset != null) {
                        if (offset == -1) {
                            return null;
                        } else {
                            return valueSerializer.deserialize(
                                    (new FileWorker(path + name.fileName + ".tab")).readFromOffset(offset));
                        }
                    }
                }
            } else {
                throw new RuntimeException("Invalid File");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void writeSysInfo() {
        configFile.refresh();
        configFile.bufferedWrite(VALIDATE_STRING);
        configFile.bufferedWrite(keySerializer.getClassString());
        configFile.bufferedWrite(valueSerializer.getClassString());
        configFile.bufferedWriteSubmit();
        configFile.refresh();
    }

    private void flushTemp() {
        String fileName = "mydbfile" + Integer.toString(nextFileNum++);
        FileWorker valueFile = new FileWorker(path + fileName + ".tab");
        FileWorker indFile = new FileWorker(path + fileName + ".ind");
        long currOffset = 0;
        valueFile.createFile();
        indFile.createFile();
        for (Map.Entry<K, ValueWrapper> entry : storageChanges.entrySet()) {
            indFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
            currOffset += valueFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
            indFile.bufferedWriteOffset(entry.getValue().deleted ? -1 : currOffset);
            currOffset += valueFile.bufferedWrite(entry.getValue().deleted ? "tombstone" :
                    valueSerializer.serialize(entry.getValue().object));
        }
        indFile.bufferedWriteSubmit();
        valueFile.bufferedWriteSubmit();
        storageChanges.clear();
        addToConfig(fileName);
    }

    private void addToConfig(String str) {
        configFile.append(str + ' ' + Long.toString(System.currentTimeMillis()));
    }
}
