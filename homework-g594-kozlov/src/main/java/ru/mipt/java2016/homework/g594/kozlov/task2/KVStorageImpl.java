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

    private static final int CACHE_SIZE = 500;

    private Integer nextFileNum = 1;

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

    private final Map<K, KeyInfo> keyMap;
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

    private class KeyInfo {
        KeyInfo(long offs, String fname) {
            offset = offs;
            filename = fname;
        }

        private long offset = -1;
        private String filename = null;
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
        keyMap = new TreeMap<K, KeyInfo>();

        try {
            if (configFile.exists()) {
                if (!initKeySet()) {
                    throw new RuntimeException("Invalid File");
                }
            }
        } catch (FileNotFoundException except) {
            configFile.createFile();
            writeSysInfo();
        }
    }

    private static class FileNames implements Comparable<FileNames> {
        FileNames(String str, long time, int siz) {
            fileName = str;
            timest = time;
            size = siz;
        }

        private String fileName;
        private Long timest;
        private int size;

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
            vect.add(new FileNames(tokens[0], Long.parseLong(tokens[1]), Integer.parseInt(tokens[3])));
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
        return keyMap.get(key) != null;
    }

    @Override
    public void write(K key, V value) {
        if (!exists(key)) {
            keyMap.put(key, new KeyInfo(-1, getCurrentFileName()));
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
            keyMap.remove(key);
            changesCheck();
        }
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
    public void close() {
        keyMap.clear();
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
            if (validateFile()) {
                for (FileNames name: workFileNames) {
                    Map<K, Long> map = loadKeysFrom(path + name.fileName);
                    for (Map.Entry<K, Long> entry: map.entrySet()) {
                        if (entry.getValue() == -1) {
                            keyMap.remove(entry.getKey());
                        } else {
                            keyMap.put(entry.getKey(), new KeyInfo(entry.getValue(), name.fileName));
                        }
                    }
                }
                for (Map.Entry<K, ValueWrapper> entry: storageChanges.entrySet()) {
                    if (entry.getValue().deleted) {
                        keyMap.remove(entry.getKey());
                    } else {
                        keyMap.put(entry.getKey(), new KeyInfo(-1, getCurrentFileName()));
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
            KeyInfo inf = keyMap.get(key);
            if (inf.filename != null) {
                FileWorker file = new FileWorker(path + inf.filename + ".tab");
                try {
                    if (file.exists()) {
                        if (inf.offset != -1) {
                            return valueSerializer.deserialize(file.readFromOffset(inf.offset));
                        } else {
                            Map<K, Long> map = loadKeysFrom(path + inf.filename);
                            Long offset = map.get(key);
                            if (offset != null) {
                                if (offset == -1) {
                                    keyMap.remove(key);
                                    return null;
                                } else {
                                    inf.offset = offset;
                                    keyMap.put(key, inf);
                                    return valueSerializer.deserialize(file.readFromOffset(offset));
                                }
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    inf.filename = null;
                    inf.offset = -1;
                }
            }
            if (validateFile()) {
                for (int i = workFileNames.length - 1; i >= 0; --i) {
                    FileNames name = workFileNames[i];
                    Map<K, Long> map = loadKeysFrom(path + name.fileName);
                    Long offset = map.get(key);
                    if (offset != null) {
                        inf.filename = name.fileName;
                        if (offset == -1) {
                            keyMap.remove(key);
                            return null;
                        } else {
                            inf.offset = offset;
                            keyMap.put(key, inf);
                            return valueSerializer.deserialize(
                                    (new FileWorker(path + name.fileName + ".tab")).readFromOffset(offset));
                        }
                    }
                }
            } else {
                throw new RuntimeException("Invalid File");
            }
        } catch (FileNotFoundException | StorageException e) {
            throw new RuntimeException(e);
        }
        keyMap.remove(key);
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
        String fileName = useCurrentFileName();
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
        addToConfig(fileName, storageChanges.size());
        storageChanges.clear();
    }

    private void addToConfig(String str, int size) {
        configFile.append(str + ' ' + Long.toString(System.currentTimeMillis()) + ' ' + Integer.toString(size));
    }

    private void merger() {
        while (!isClosedFlag) {
            try {
                validateFile();
                if (workFileNames.length < 2) {
                    Thread.sleep(10);
                    continue;
                }
                int firstSize = workFileNames[workFileNames.length - 1].size;
                int diffInSize = Math.abs(workFileNames[0].size - firstSize);
                int indSecond = 0;
                for (int i = 1; i < workFileNames.length - 1; ++i) {
                    if (Math.abs(workFileNames[i].size - firstSize) < diffInSize) {
                        indSecond = i;
                        diffInSize = Math.abs(workFileNames[i].size - firstSize);
                    }
                }
                merge(workFileNames[workFileNames.length - 1], workFileNames[indSecond]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String useCurrentFileName() {
        synchronized (nextFileNum) {
            return "mydbfile" + Integer.toString(nextFileNum++);
        }
    }

    private  String getCurrentFileName() {
        synchronized (nextFileNum) {
            return "mydbfile" + Integer.toString(nextFileNum);
        }
    }

    private void merge(FileNames latest, FileNames second) {
        Map<K, Long> latestKeys = loadKeysFrom(path + latest.fileName);
        Map<K, Long> secondKeys = loadKeysFrom(path + second.fileName);
        FileWorker latestFile = new FileWorker(path + latest.fileName + ".tab");
        FileWorker secondFile = new FileWorker(path + second.fileName + ".tab");
        String fileName = useCurrentFileName();
        FileWorker valueFile = new FileWorker(path + fileName + ".tab");
        FileWorker indFile = new FileWorker(path + fileName + ".ind");

    }
}

