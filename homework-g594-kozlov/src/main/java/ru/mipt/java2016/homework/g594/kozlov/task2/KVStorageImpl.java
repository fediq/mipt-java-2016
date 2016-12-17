package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.SerializerInterface;

import java.io.File;
import java.util.*;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class KVStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private static final int CACHE_SIZE = 800;
    private Integer nextFileNum = 1;
    private final FileWorker configFile;
    private FileNames[] workFileNames = null;
    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final String path;
    private final Map<K, V> storageChanges = new TreeMap<K, V>();
    private final Set<K> deleteChanges = new TreeSet<K>();
    private final Comparator<K> keyComparator;
    /*private LoadingCache<K, V> cacheValues = CacheBuilder.newBuilder()
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
                    });*/
    private final Map<K, KeyInfo> keyMap;
    private static final String VALIDATE_STRING = "itismyawesomestoragedontfakeit";
    private Boolean isClosedFlag = false;
    private final Thread threadMerger = new Thread(new Merger());

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
        configFile = new FileWorker(path + "mydbconfig.db", false);
        keyMap = Collections.synchronizedMap(new HashMap<K, KeyInfo>());
        if (configFile.exists()) {
            if (!initKeySet()) {
                throw new RuntimeException("Invalid File");
            }
        } else {
            configFile.createFile();
            writeSysInfo();
        }
        threadMerger.start();
    }

    private static class FileNames implements Comparable<FileNames> {
        FileNames(String str, long time) {
            fileName = str;
            timest = time;
        }

        private String fileName;
        private Long timest;

        @Override
        public int compareTo(FileNames other) {
            return Long.compare(timest, other.timest);
        }
    }

    private boolean validateFile() {
        if (!configFile.exists()) {
            return false;
        }
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
            V value = storageChanges.get(key);
            if (value != null) {
                return value;
            }
            return loadKey(key);
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
        deleteChanges.remove(key);
        storageChanges.put(key, value);
        changesCheck();
    }

    @Override
    public void delete(K key) {
        if (exists(key)) {
            storageChanges.remove(key);
            deleteChanges.add(key);
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
        flushDeletions();
        configFile.close();
    }

    private void changesCheck() {
        if (storageChanges.size() >= CACHE_SIZE) {
            flushTemp();
            storageChanges.clear();
        }
    }

    private void flushDeletions() {
        String fileName = path + "deletes.db";
        FileWorker dfile = new FileWorker(fileName, false);
        if (!dfile.exists()) {
            dfile.createFile();
        }
        for (K entry : deleteChanges) {
            dfile.bufferedWrite(keySerializer.serialize(entry));
        }
        dfile.bufferedWriteSubmit();
        deleteChanges.clear();
    }

    private boolean initKeySet() {
        synchronized (configFile) {
            if (validateFile()) {
                for (FileNames name : workFileNames) {
                    Map<K, Long> map = loadKeysFrom(path + name.fileName);
                    for (Map.Entry<K, Long> entry : map.entrySet()) {
                        keyMap.put(entry.getKey(), new KeyInfo(entry.getValue(), name.fileName));
                    }
                }
                for (K keys: storageChanges.keySet()) {
                    keyMap.put(keys, new KeyInfo(-1, null));
                }
                FileWorker dfile = new FileWorker(path + "deletes.db", false);
                if (!dfile.exists()) {
                    return false;
                }
                String keyString = dfile.readNextToken();
                while (keyString != null) {
                    try {
                        K key = keySerializer.deserialize(keyString);
                        deleteChanges.add(key);
                        keyMap.remove(key);
                    } catch (StorageException e) {
                        throw new RuntimeException(e);
                    }
                    keyString = dfile.readNextToken();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private Map<K, Long> loadKeysFrom(String fileName) {
        FileWorker file = new FileWorker(fileName + ".ind", false);
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
        file.close();
        return map;
    }

    private V loadKey(K key) {
        V result = null;
        synchronized (configFile) {
            KeyInfo inf = keyMap.get(key);
            try (FileWorker file = new FileWorker(path + inf.filename + ".tab", false)) {
                if (file.exists() && inf.filename != null) {
                    file.moveToOffset(inf.offset);
                    K thisKey = keySerializer.deserialize(file.readNextToken());
                    if (key.equals(thisKey)) {
                        result = valueSerializer.deserialize(file.readNextToken());
                    }
                } else {
                    //System.out.println("Missed");
                    System.out.println(inf.filename);
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

    private void writeSysInfo() {
        configFile.bufferedWrite(VALIDATE_STRING);
        configFile.bufferedWrite(keySerializer.getClassString());
        configFile.bufferedWrite(valueSerializer.getClassString());
        configFile.bufferedWriteSubmit();
        configFile.close();
    }

    private void flushTemp() {
        String fileName = useCurrentFileName();
        FileWorker valueFile = new FileWorker(path + fileName + ".tab", false);
        FileWorker indFile = new FileWorker(path + fileName + ".ind", false);
        Map<K, Long> offsetMap = new TreeMap<K, Long>();
        long currOffset = 0;
        valueFile.createFile();
        indFile.createFile();
        for (Map.Entry<K, V> entry : storageChanges.entrySet()) {
            currOffset = writeTo(offsetMap, valueFile, entry.getKey(),
                    valueSerializer.serialize(entry.getValue()), currOffset);
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
        addToConfig(fileName, storageChanges.size());
        storageChanges.clear();
    }

    private void addToConfig(String str, int size) {
        synchronized (configFile) {
            configFile.append(str + ' ' + Long.toString(System.currentTimeMillis()));
        }
    }

    private class Merger implements Runnable {
        public void run() {
            while (!isClosedFlag) {
                try {
                    FileNames first;
                    FileNames second;
                    synchronized (configFile) {
                        validateFile();
                        if (workFileNames.length < 2) {
                            Thread.sleep(1);
                            continue;
                        }
                        first = workFileNames[workFileNames.length - 1];
                        second = workFileNames[workFileNames.length - 2];
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
        FileWorker latestFl = new FileWorker(path + latest.fileName + ".tab", false);
        FileWorker secondFl = new FileWorker(path + second.fileName + ".tab", false);
        BufferedQueue latestFile = new BufferedQueue(latestFl);
        BufferedQueue secondFile = new BufferedQueue(secondFl);
        String fileName = useCurrentFileName();
        //System.out.println("merging to " + fileName);
        FileWorker valueFile = new FileWorker(path + fileName + ".tab", false);
        FileWorker indFile = new FileWorker(path + fileName + ".ind", false);
        Map<K, Long> offsetMap = new TreeMap<>();
        valueFile.createFile();
        indFile.createFile();
        long currOffset = 0;
        K keyFromFirst = null;
        boolean isFromFirst;
        K keyFromSecond = null;
        try {
            synchronized (configFile) {
                keyFromFirst = keySerializer.deserialize(latestFile.getNext());
                keyFromSecond = keySerializer.deserialize(secondFile.getNext());
            }
            while (keyFromFirst != null || keyFromSecond != null) {
                if (keyFromFirst == null) {
                    isFromFirst = false;
                } else {
                    if (keyFromSecond == null) {
                        isFromFirst = true;
                    } else {
                        if (keyComparator.compare(keyFromFirst, keyFromSecond) == 0) {
                            isFromFirst = true;
                            synchronized (configFile) {
                                secondFile.getNext();
                                keyFromSecond = keySerializer.deserialize(secondFile.getNext());
                            }
                        } else {
                            if (keyComparator.compare(keyFromFirst, keyFromSecond) < 0) {
                                isFromFirst = true;
                            } else {
                                isFromFirst = false;
                            }
                        }
                    }
                }
                synchronized (configFile) {
                    if (isFromFirst) {
                        currOffset = writeTo(offsetMap, valueFile, keyFromFirst, latestFile.getNext(), currOffset);
                        keyFromFirst = keySerializer.deserialize(latestFile.getNext());
                    } else {
                        currOffset = writeTo(offsetMap, valueFile, keyFromSecond, secondFile.getNext(), currOffset);
                        keyFromSecond = keySerializer.deserialize(secondFile.getNext());
                    }
                }
            }
            valueFile.bufferedWriteSubmit();
            for (Map.Entry<K, Long> entry : offsetMap.entrySet()) {
                indFile.bufferedWrite(keySerializer.serialize(entry.getKey()));
                indFile.bufferedWriteOffset(entry.getValue());
                keyMap.put(entry.getKey(), new KeyInfo(entry.getValue(), fileName));
            }
            indFile.bufferedWriteSubmit();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        synchronized (configFile) {
            rewriteConfig(fileName, second.timest, latest.fileName, second.fileName);
            //System.out.println(latest.fileName + ' ' + second.fileName + " deleted");
            latestFl.delete();
            secondFl.delete();
            latestFl = new FileWorker(path + latest.fileName + ".ind", false);
            secondFl = new FileWorker(path + second.fileName + ".ind", false);
            latestFl.delete();
            secondFl.delete();
        }
    }

    private void rewriteConfig(String fileName, long timest, String delete1, String delete2) {
        synchronized (configFile) {
            if (!validateFile()) {
                throw new RuntimeException("config corrupted");
            }
            writeSysInfo();
            for (FileNames fname: workFileNames) {
                if (!fname.fileName.equals(delete1) && !fname.fileName.equals(delete2)) {
                    configFile.append(fname.fileName + ' ' + Long.toString(fname.timest));
                }
            }
            configFile.append(fileName + ' ' + Long.toString(timest));
        }
    }

    private long writeTo(Map<K, Long> offsetMap, FileWorker valueFile, K key, String value, long currOffset) {
        offsetMap.put(key, currOffset);
        currOffset += valueFile.bufferedWrite(keySerializer.serialize(key));
        currOffset += valueFile.bufferedWrite(value);
        return currOffset;
    }
}

