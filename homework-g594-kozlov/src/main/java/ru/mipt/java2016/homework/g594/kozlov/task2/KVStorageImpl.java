package ru.mipt.java2016.homework.g594.kozlov.task2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class KVStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private static final int CACHE_SIZE = 750;
    private Integer nextFileNum = 1;
    private final FileWorker configFile;
    private Vector<FileNames> workFileNames = null;
    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final String path;
    private final Map<K, ValueWrapper> storageChanges = new TreeMap<K, ValueWrapper>();
    private final Comparator<K> keyComparator;
    private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
            .softValues()
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
        keyMap = new HashMap<K, KeyInfo>();
        if (configFile.exists()) {
            if (!validateFile()) {
                throw new RuntimeException("Invalid File");
            }
            initKeySet();
        } else {
            configFile.createFile();
            writeSysInfo();
            if (!validateFile()) {
                System.out.println("valid error");
            }
        }
        threadMerger.start();
    }

    private static class FileNames implements Comparable<FileNames> {
        FileNames(String str, long time, FileWorker fl) {
            fileName = str;
            timest = time;
            file = fl;
        }

        private String fileName;
        private Long timest;
        private FileWorker file;

        @Override
        public int compareTo(FileNames other) {
            return Long.compare(timest, other.timest);
        }
    }

    private boolean validateFile() {
        if (!configFile.exists()) {
            return false;
        }
        configFile.close();
        byte[] token = configFile.readNextToken();
        String tok = new String(token);
        if (token == null || !tok.equals(VALIDATE_STRING)) {

            return false;
        }
        token = configFile.readNextToken();
        tok = new String(token);
        if (token == null || !tok.equals(keySerializer.getClassString())) {
            return false;
        }
        token = configFile.readNextToken();
        tok = new String(token);
        if (token == null || !tok.equals(valueSerializer.getClassString())) {
            return false;
        }
        Vector<FileNames> vect = new Vector<>();
        token = configFile.readNextToken();
        while (token != null) {
            String[] tokens = new String(token).split(" ");
            vect.add(new FileNames(tokens[0], Long.parseLong(tokens[1]), new FileWorker(path + tokens[0] + ".tab")));
            int fileNum = Integer.parseInt(tokens[0].substring(8, tokens[0].length()));
            if (fileNum >= nextFileNum) {
                nextFileNum = fileNum + 1;
            }
            token = configFile.readNextToken();
        }
        if (workFileNames != null) {
            for (FileNames files: workFileNames) {
                files.file.close();
            }
        }
        workFileNames = vect;
        configFile.close();
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
                throw new RuntimeException(e);
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
            keyMap.put(key, new KeyInfo(-1, null));
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
        for (FileNames files: workFileNames) {
            files.file.close();
        }
        configFile.close();
        try {
            threadMerger.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void changesCheck() {
        if (storageChanges.size() >= CACHE_SIZE) {
            flushTemp();
            storageChanges.clear();
        }
    }

    private void initKeySet() {
        synchronized (configFile) {
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
                    keyMap.put(entry.getKey(), new KeyInfo(-1, null));
                }
            }
        }
    }

    private Map<K, Long> loadKeysFrom(String fileName) {
        FileWorker file = new FileWorker(fileName + ".ind");
        Map<K, Long> map = new TreeMap<K, Long>();
        byte[] nextKey = file.readNextToken();
        while (nextKey != null) {
            Long offset = file.readLong();
            try {
                map.put(keySerializer.deserialize(nextKey), offset);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            nextKey = file.readNextToken();
        }
        file.close();
        return map;
    }

    private V loadKey(K key) {
        V result = null;
        synchronized (configFile) {
            KeyInfo inf = keyMap.get(key);
            try {
                FileWorker file = null;
                for (FileNames names: workFileNames) {
                    if (names.fileName.equals(inf.filename)) {
                        file = names.file;
                    }
                }
                if (file ==  null) {
                    System.out.println(inf.filename);
                }
                if (file.exists() && inf.filename != null) {
                    if (inf.offset != -1) {
                        file.moveToOffset(inf.offset);
                        K thisKey = keySerializer.deserialize(file.readNextToken());
                        if (key.equals(thisKey)) {
                            result = valueSerializer.deserialize(file.readNextToken());
                        }
                    } else {
                        Map<K, Long> map = loadKeysFrom(path + inf.filename);
                        Long offset = map.get(key);
                        if (offset != null) {
                            if (offset == -1) {
                                keyMap.remove(key);
                                result = null;
                            } else {
                                inf.offset = offset;
                                keyMap.put(key, inf);
                                file.moveToOffset(inf.offset);
                                K thisKey = keySerializer.deserialize(file.readNextToken());
                                if (key.equals(thisKey)) {
                                    result = valueSerializer.deserialize(file.readNextToken());
                                } else {
                                    initKeySet();
                                    result = loadKey(key);
                                }
                            }
                        } else {
                            initKeySet();
                            result = loadKey(key);
                        }
                    }
                } else {
                    initKeySet();
                    result = loadKey(key);
                }
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
        }
        if (result == null) {
            keyMap.remove(key);
        }
        return result;
    }

    private V buffLoad(K key) {
        V result = null;
        synchronized (configFile) {
            KeyInfo inf = keyMap.get(key);
            for (FileNames names : workFileNames) {
                if (names.fileName.equals(inf.filename)) {
                    names.file.moveToOffset(inf.offset);
                    try {
                        K thisKey = keySerializer.deserialize(names.file.readNextToken());
                        if (key.equals(thisKey)) {
                            result = valueSerializer.deserialize(names.file.readNextToken());
                        }
                    } catch (StorageException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }

    private void writeSysInfo() {
        configFile.close();
        configFile.bufferedWrite(VALIDATE_STRING.getBytes());
        configFile.bufferedWrite(keySerializer.getClassString().getBytes());
        configFile.bufferedWrite(valueSerializer.getClassString().getBytes());
        configFile.bufferedWriteSubmit();
        configFile.close();
    }

    private void flushTemp() {
        String fileName = useCurrentFileName();
        FileWorker valueFile = new FileWorker(path + fileName + ".tab");
        FileWorker indFile = new FileWorker(path + fileName + ".ind");
        Map<K, Long> offsetMap = new TreeMap<K, Long>();
        long currOffset = 0;
        valueFile.createFile();
        indFile.createFile();
        for (Map.Entry<K, ValueWrapper> entry : storageChanges.entrySet()) {
            offsetMap.put(entry.getKey(), entry.getValue().deleted ? -1 : currOffset);
            currOffset += valueFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
            currOffset += valueFile.bufferedWrite(entry.getValue().deleted ?
                    "tombstone".getBytes() : valueSerializer.serialize(entry.getValue().object));
        }
        valueFile.bufferedWriteSubmit();
        for (Map.Entry<K, Long> entry : offsetMap.entrySet()) {
            indFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
            indFile.bufferedWriteOffset(entry.getValue());
            if (entry.getValue() != -1) {
                keyMap.put(entry.getKey(), new KeyInfo(entry.getValue(), fileName));
            }
        }
        indFile.bufferedWriteSubmit();
        indFile.close();
        valueFile.close();
        addToConfig(fileName, valueFile);
        storageChanges.clear();
    }

    private void addToConfig(String str, FileWorker file) {
        synchronized (configFile) {
            long time = System.currentTimeMillis();
            workFileNames.add(new FileNames(str, time, file));
            configFile.append(str + ' ' + Long.toString(time));
        }
    }

    private class Merger implements Runnable {
        public void run() {
            while (!isClosedFlag) {
                try {
                    FileNames first;
                    FileNames second;
                    synchronized (configFile) {
                        if (workFileNames.size() < 2) {
                            Thread.sleep(10);
                            continue;
                        }
                        first = workFileNames.elementAt(workFileNames.size() - 1);
                        second = workFileNames.elementAt(workFileNames.size() - 2);
                    }
                    merge(first, second);
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

    private void merge(FileNames latest, FileNames second) {
        FileWorker latestFile = new FileWorker(path + latest.fileName + ".tab");
        FileWorker secondFile = new FileWorker(path + second.fileName + ".tab");
        String fileName = useCurrentFileName();
        FileWorker valueFile = new FileWorker(path + fileName + ".tab");
        FileWorker indFile = new FileWorker(path + fileName + ".ind");
        Map<K,  Pair<Integer, Long>> offsetMap = new TreeMap<K, Pair<Integer, Long>>();
        valueFile.createFile();
        indFile.createFile();
        long currOffset = 0;
        K keyFromFirst = null;
        K keyFromSecond = null;
        boolean writeFromFirst = true;
        try {
            keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
            keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
            while (keyFromFirst != null || keyFromSecond != null) {
                if (keyFromFirst == null) {
                    writeFromFirst = false;
                } else {
                    if (keyFromSecond == null) {
                        writeFromFirst = true;
                    } else {
                        if (keyComparator.compare(keyFromFirst, keyFromSecond) == 0) {
                            writeFromFirst = true;
                            secondFile.readNextToken();
                            keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
                        } else {
                            if (keyComparator.compare(keyFromFirst, keyFromSecond) < 0) {
                                writeFromFirst = true;
                            } else {
                                writeFromFirst = false;
                            }
                        }
                    }
                }
                if (writeFromFirst) {
                    offsetMap.put(keyFromFirst, new Pair(1, currOffset));
                    currOffset += valueFile.bufferedWrite(keySerializer.serialize(keyFromFirst));
                    currOffset += valueFile.bufferedWrite(latestFile.readNextToken());
                    keyFromFirst = keySerializer.deserialize(latestFile.readNextToken());
                } else {
                    offsetMap.put(keyFromSecond, new Pair(2, currOffset));
                    currOffset += valueFile.bufferedWrite(keySerializer.serialize(keyFromSecond));
                    currOffset += valueFile.bufferedWrite(secondFile.readNextToken());
                    keyFromSecond = keySerializer.deserialize(secondFile.readNextToken());
                }
            }
            valueFile.bufferedWriteSubmit();
            for (Map.Entry<K, Pair<Integer, Long>> entry : offsetMap.entrySet()) {
                indFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
                indFile.bufferedWriteOffset(entry.getValue().getValue());
            }
            indFile.bufferedWriteSubmit();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        synchronized (configFile) {
            rewriteConfig(fileName, second.timest, latest.fileName, second.fileName, valueFile);
            for (Map.Entry<K, Pair<Integer, Long>> entry : offsetMap.entrySet()) {
                if (entry.getValue().getValue() != -1) {
                    keyMap.put(entry.getKey(), new KeyInfo(entry.getValue().getValue(), fileName));
                }
            }
        }
        latestFile.delete();
        secondFile.delete();
        latestFile = new FileWorker(path + latest.fileName + ".ind");
        secondFile = new FileWorker(path + second.fileName + ".ind");
        latestFile.delete();
        secondFile.delete();
    }

    private void rewriteConfig(String fileName, long timest, String delete1, String delete2,  FileWorker file) {
        synchronized (configFile) {
            writeSysInfo();
            Vector<FileNames> vect = new Vector<>();
            for (FileNames fname: workFileNames) {
                if (!fname.fileName.equals(delete1) && !fname.fileName.equals(delete2)) {
                    configFile.append(fname.fileName + ' ' + Long.toString(fname.timest));
                    vect.add(fname);
                } else {
                    fname.file.close();
                }
                if (fname.fileName.equals(delete2)) {
                    vect.add(new FileNames(fileName, timest, file));
                    configFile.append(fileName + ' ' + Long.toString(timest));
                }
            }
            workFileNames = vect;
        }
    }
}

