package ru.mipt.java2016.homework.g597.dmitrieva.task3;

import ru.mipt.java2016.homework.g597.dmitrieva.task2.SerializationStrategy;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by irinadmitrieva on 19.11.16.
 */
public class OptimizedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_SIZE_OF_CACHE = 20;
    private static final int MAX_SIZE_OF_KEY_AND_VALUE_MAP = 1200;

    private class Position {
        private int fileNumber;
        private long offset;

        Position(int fileNumber, long offset) {
            this.fileNumber = fileNumber;
            this.offset = offset;
        }
    }

    private LinkedHashMap<K, V> cacheMap;
    private HashMap<K, Position> keyAndPositionMap;
    private HashMap<K, V> keyAndValueMap;
    private HashSet<K> presenceSet;
    private ArrayList<RandomAccessFile> randAccFilesArray;

    private SerializationStrategy<K> keyStrategy;
    private SerializationStrategy<V> valueStrategy;

    private String fileWithPositionsPathname;
    private String baseName;
    private final String mode = "rw"; // По умолчанию выставили чтение/запись
    private boolean isStorageOpened;

    private void checkStorageNotClosed() throws IllegalStateException {
        if (!isStorageOpened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    private String getFileName(String mainFileName, Integer i) {
        return mainFileName + i.toString() + ".txt";
    }

    public OptimizedKeyValueStorage(String path, SerializationStrategy<K> keyStrategy,
                                    SerializationStrategy<V> valueStrategy)
            throws IOException {
        if (path == null) {
            throw new NullPointerException("The pathname argument is null");
        }

        isStorageOpened = true;
        this.keyStrategy = keyStrategy;
        this.valueStrategy = valueStrategy;
        fileWithPositionsPathname = path + File.separator + "storage.txt";
        baseName = path + File.separator + "storage";

        cacheMap = new LinkedHashMap<K, V>();
        keyAndPositionMap = new HashMap<K, Position>();
        keyAndValueMap = new HashMap<K, V>();
        presenceSet = new HashSet<K>();
        randAccFilesArray = new ArrayList<RandomAccessFile>();

        try {
            File file = new File(fileWithPositionsPathname);
            if (file.createNewFile()) {
                RandomAccessFile randAccFile = new RandomAccessFile(file, mode);
                try {
                    randAccFile.writeInt(0);
                    randAccFile.writeInt(0);
                } catch (IOException e) {
                    throw new IOException("Couldn't write during initialization of files");
                }
            } else {
                RandomAccessFile randAccFile = new RandomAccessFile(file, mode);
                int numberOfFiles = randAccFile.readInt();
                for (int i = 0; i < numberOfFiles; i++) {
                    File currentFile = new File(getFileName(baseName, i));
                    if (!currentFile.exists()) {
                        throw new IllegalStateException("No such file");
                    }
                    randAccFilesArray.add(new RandomAccessFile(currentFile, mode));
                }
                int sizeOfKeyAndPositionMap = randAccFile.readInt();
                for (int i = 0; i < sizeOfKeyAndPositionMap; i++) {
                    try {
                        K key = this.keyStrategy.read(randAccFile);
                        int fileNumber = randAccFile.readInt();
                        long offset = randAccFile.readLong();
                        keyAndPositionMap.put(key, new Position(fileNumber, offset));
                        presenceSet.add(key);
                    } catch (IOException e) {
                        throw new IOException("Couldn't read from file during opening of the storage");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("The given string does not denote an existing file");
        }
    }

    @Override
    public V read(K key) {
        checkStorageNotClosed();
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        }
        if (keyAndValueMap.containsKey(key)) {
            return keyAndValueMap.get(key);
        }
        if (!keyAndPositionMap.containsKey(key)) {
            return null;
        }
        int fileNumber = keyAndPositionMap.get(key).fileNumber;
        long offset = keyAndPositionMap.get(key).offset;
        RandomAccessFile currentFile = randAccFilesArray.get(fileNumber);
        try {
            currentFile.seek(offset);
            V value = valueStrategy.read(currentFile);
            cacheMap.put(key, value);
            if (cacheMap.size() > MAX_SIZE_OF_CACHE) {
                Iterator<Map.Entry<K, V>> iterator = cacheMap.entrySet().iterator();
                while (cacheMap.size() > MAX_SIZE_OF_CACHE) {
                    iterator.next();
                    iterator.remove();
                }
            }
            return value;
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't find needed data in file");
        }
    }

    private void dropKeyAndValueMapOnDisk() throws IOException {
        int newFileNumber = randAccFilesArray.size();
        File newFile = new File(getFileName(baseName, newFileNumber));
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            throw new IOException("Couldn't create file during dropping map with keys and values");
        }
        randAccFilesArray.add(new RandomAccessFile(newFile, mode));
        RandomAccessFile currentFile = randAccFilesArray.get(newFileNumber);
        currentFile.setLength(0);
        currentFile.seek(0);
        for (Map.Entry<K, V> entry: keyAndValueMap.entrySet()) {
            if (null == entry.getValue()) {
                keyAndPositionMap.remove(entry.getKey());
                continue;
            }
            Position newPosition = new Position(newFileNumber, currentFile.getFilePointer());
            keyAndPositionMap.put(entry.getKey(), newPosition);
            try {
                this.valueStrategy.write(currentFile, entry.getValue());
            } catch (IOException e) {
                throw new IOException("Couldn't wrote during dropping map with keys and values");
            }
        }
        keyAndValueMap.clear();
    }

    @Override
    public void write(K key, V value) {
        checkStorageNotClosed();
        keyAndValueMap.put(key, value);
        presenceSet.add(key);
        if (keyAndValueMap.size() > MAX_SIZE_OF_KEY_AND_VALUE_MAP) {
            try {
                dropKeyAndValueMapOnDisk();
            } catch (IOException e) {
                throw new IllegalStateException("Failed during dropping map with keys and values");
            }
        }
    }

    @Override
    public boolean exists(K key) {
        checkStorageNotClosed();
        return presenceSet.contains(key);
    }

    @Override
    public void delete(K key) {
        checkStorageNotClosed();
        if (exists(key)) {
            cacheMap.remove(key);
            keyAndPositionMap.remove(key);
            presenceSet.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkStorageNotClosed();
        return presenceSet.iterator();
    }

    @Override
    public int size() {
        checkStorageNotClosed();
        return presenceSet.size();
    }

    @Override
    public void close() throws IOException {
        isStorageOpened = false;
        dropKeyAndValueMapOnDisk();
        try {
            RandomAccessFile randAccFile = new RandomAccessFile(this.fileWithPositionsPathname, mode);
            randAccFile.writeInt(randAccFilesArray.size());
            randAccFile.writeInt(keyAndPositionMap.size());
            for (Map.Entry<K, Position> entry : keyAndPositionMap.entrySet()) {
                keyStrategy.write(randAccFile, entry.getKey());
                randAccFile.writeInt(entry.getValue().fileNumber);
                randAccFile.writeLong(entry.getValue().offset);
            }
        } catch (IOException e) {
            throw new IOException("Random access file wasn't created");
        }
    }
}
