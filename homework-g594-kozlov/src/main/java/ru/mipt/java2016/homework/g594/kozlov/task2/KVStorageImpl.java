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
    private final Comparator<K> keyComparator;
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
    private final Thread threadMerger = new Thread(new Merger());

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
                         SerializerInterface<V> valueSerializer, Comparator<K> keyComparator) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.keyComparator = keyComparator;
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
        threadMerger.start();
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
            vect.add(new FileNames(tokens[0], Long.parseLong(tokens[1]), Integer.parseInt(tokens[2])));
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
        try {
            threadMerger.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            synchronized (configFile) {
                if (validateFile()) {
                    for (FileNames name : workFileNames) {
                        Map<K, Long> map = loadKeysFrom(path + name.fileName);
                        for (Map.Entry<K, Long> entry : map.entrySet()) {
                            if (entry.getValue() == -1) {
                                keyMap.remove(entry.getKey());
                            } else {
                                keyMap.put(entry.getKey(), new KeyInfo(entry.getValue(), name.fileName));
                            }
                        }
                    }
                    for (Map.Entry<K, ValueWrapper> entry : storageChanges.entrySet()) {
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
            synchronized (configFile) {
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
            currOffset = writeTo(indFile, valueFile, entry.getKey(), entry.getValue().deleted,
                    valueSerializer.serialize(entry.getValue().object), currOffset);
        }
        indFile.bufferedWriteSubmit();
        valueFile.bufferedWriteSubmit();
        addToConfig(fileName, storageChanges.size());
        storageChanges.clear();
    }

    private void addToConfig(String str, int size) {
        synchronized (configFile) {
            configFile.append(str + ' ' + Long.toString(System.currentTimeMillis()) + ' ' + Integer.toString(size));
        }
    }

    private class Merger implements Runnable {
        public void run() {
            while (!isClosedFlag) {
                try {
                    int indSecond = 0;
                    synchronized (configFile) {
                        validateFile();
                        if (workFileNames.length < 2) {
                            Thread.sleep(10);
                            continue;
                        }
                        int firstSize = workFileNames[workFileNames.length - 1].size;
                        int diffInSize = Math.abs(workFileNames[0].size - firstSize);
                        for (int i = 1; i < workFileNames.length - 1; ++i) {
                            if (Math.abs(workFileNames[i].size - firstSize) < diffInSize) {
                                indSecond = i;
                                diffInSize = Math.abs(workFileNames[i].size - firstSize);
                            }
                        }
                    }
                    merge(workFileNames[workFileNames.length - 1], workFileNames[indSecond]);
                    System.out.println("merged");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        FileWorker latestFile = new FileWorker(path + latest.fileName + ".tab");
        FileWorker secondFile = new FileWorker(path + second.fileName + ".tab");
        String fileName = useCurrentFileName();
        FileWorker valueFile = new FileWorker(path + fileName + ".tab");
        FileWorker indFile = new FileWorker(path + fileName + ".ind");
        valueFile.createFile();
        indFile.createFile();
        long currOffset = 0;
        int size = 0;
        K keyFromFirst = null;
        String valueFromFirst = null;
        String valueFromSecond = null;
        K keyFromSecond = null;
        try {
            keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
            keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
            while (keyFromFirst != null || keyFromSecond != null) {
                if (keyFromFirst == null) {
                    valueFromSecond = secondFile.readNextToken();
                    writeTo(indFile, valueFile, keyFromSecond, false, valueFromSecond, currOffset);
                    keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
                    size++;
                    continue;
                }
                if (keyFromSecond == null) {
                    valueFromFirst = latestFile.readNextToken();
                    writeTo(indFile, valueFile, keyFromFirst, false, valueFromFirst, currOffset);
                    keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
                    size++;
                    continue;
                }
                if (keyComparator.compare(keyFromFirst, keyFromSecond) == 0) {
                    valueFromFirst = latestFile.readNextToken();
                    writeTo(indFile, valueFile, keyFromFirst, false, valueFromFirst, currOffset);
                    keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
                    valueFromSecond = secondFile.readNextToken();
                    keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
                    size++;
                } else {
                    if (keyComparator.compare(keyFromFirst, keyFromSecond) < 0) {
                        valueFromFirst = latestFile.readNextToken();
                        writeTo(indFile, valueFile, keyFromFirst, false, valueFromFirst, currOffset);
                        keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
                        size++;
                    } else {
                        valueFromSecond = secondFile.readNextToken();
                        writeTo(indFile, valueFile, keyFromSecond, false, valueFromSecond, currOffset);
                        keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
                        size++;
                    }
                }
            }
            indFile.bufferedWriteSubmit();
            valueFile.bufferedWriteSubmit();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        rewriteConfig(fileName, size, latest.fileName, second.fileName);
    }

    private void rewriteConfig(String fileName, int size, String delete1, String delete2) {
        synchronized (configFile) {
            try {
                validateFile();
                writeSysInfo();
                for (FileNames fname: workFileNames) {
                    if (!fname.fileName.equals(delete1) && !fname.fileName.equals(delete2)) {
                        configFile.append(fname.fileName + ' ' + Long.toString(fname.timest)
                                + ' ' + Integer.toString(fname.size));
                    }
                }
                addToConfig(fileName, size);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private long writeTo(FileWorker indFile, FileWorker valueFile, K key,
                         boolean deleted, String value, long currOffset) {
        indFile.bufferedWrite(keySerializer.serialize(key));
        currOffset += valueFile.bufferedWrite(keySerializer.serialize(key));
        indFile.bufferedWriteOffset(deleted ? -1 : currOffset);
        currOffset += valueFile.bufferedWrite(deleted ? "tombstone" : value);
        return  currOffset;
    }
}

