package ru.mipt.java2016.homework.g597.dmitrieva.task3;

import ru.mipt.java2016.homework.g597.dmitrieva.task2.SerializationStrategy;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by irinadmitrieva on 19.11.16.
 */
public class OptimizedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_SIZE_OF_KEY_AND_VALUE_MAP = 500;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private HashMap<K, Long> keyAndOffsetMap;
    private Map<K, V> keyAndValueMap;
    private HashSet<K> presenceSet;
    private RandomAccessFile randAccFileStorage;

    private SerializationStrategy<K> keyStrategy;
    private SerializationStrategy<V> valueStrategy;

    private String fileWithOffsetsPathname;
    private String fileStoragePathname;
    private final String mode = "rw"; // По умолчанию выставили чтение/запись
    private boolean isStorageOpened = true;
    private FileLock storageLock;

    private void checkStorageNotClosed() throws IllegalStateException {
        if (!isStorageOpened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    public OptimizedKeyValueStorage(String path, SerializationStrategy<K> keyStrategy,
                                    SerializationStrategy<V> valueStrategy)
            throws IOException {
        if (path == null) {
            throw new NullPointerException("The pathname argument is null");
        }

        this.keyStrategy = keyStrategy;
        this.valueStrategy = valueStrategy;
        fileWithOffsetsPathname = path + File.separator + "offsets.txt";
        fileStoragePathname = path + File.separator + "storage.txt";

        keyAndOffsetMap = new HashMap<K, Long>();
        keyAndValueMap = new HashMap<K, V>();
        presenceSet = new HashSet<K>();

        try {
            File fileWithOffsets = new File(fileWithOffsetsPathname);
            File fileStorage = new File(fileStoragePathname);
            if (fileWithOffsets.createNewFile()) {
                RandomAccessFile randAccFileWithOffsets = new RandomAccessFile(fileWithOffsets, mode);
                if (fileStorage.createNewFile()) {
                    randAccFileStorage = new RandomAccessFile(fileStorage, mode);
                }
                try {
                    storageLock = randAccFileWithOffsets.getChannel().lock();
                } catch (OverlappingFileLockException e) {
                    throw new IllegalStateException("Storage is already being used");
                }
                try {
                    randAccFileWithOffsets.writeInt(0);
                } catch (IOException e) {
                    throw new IOException("Couldn't write during initialization of files");
                }
            } else {
                RandomAccessFile randAccFileWithOffsets = new RandomAccessFile(fileWithOffsets, mode);
                randAccFileStorage = new RandomAccessFile(fileStorage, mode);

                try {
                    storageLock = randAccFileWithOffsets.getChannel().lock();
                } catch (OverlappingFileLockException e) {
                    throw new IllegalStateException("Storage is already being used");
                }

                int sizeOfKeyAndOffsetMap = randAccFileWithOffsets.readInt();
                for (int i = 0; i < sizeOfKeyAndOffsetMap; i++) {
                    try {
                        K key = this.keyStrategy.read(randAccFileWithOffsets);
                        long offset = randAccFileWithOffsets.readLong();
                        keyAndOffsetMap.put(key, offset);
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
        lock.readLock().lock();
        try {
            checkStorageNotClosed();

            if (keyAndValueMap.containsKey(key)) {
                return keyAndValueMap.get(key);
            }
            if (!keyAndOffsetMap.containsKey(key)) {
                return null;
            }
            long offset = keyAndOffsetMap.get(key);
            randAccFileStorage.seek(offset);
            V value = valueStrategy.read(randAccFileStorage);
            return value;
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't find needed data in file");
        } finally {
            lock.readLock().unlock();
        }
    }

    private void dropKeyAndValueMapOnDisk() throws IOException {
        lock.writeLock().lock();
        try {
            randAccFileStorage.seek(randAccFileStorage.length());
            for (Map.Entry<K, V> entry : keyAndValueMap.entrySet()) {
                if (null == entry.getValue()) {
                    keyAndOffsetMap.remove(entry.getKey());
                    continue;
                }
                keyAndOffsetMap.put(entry.getKey(), randAccFileStorage.getFilePointer());
                try {
                    this.valueStrategy.write(randAccFileStorage, entry.getValue());
                } catch (IOException e) {
                    throw new IOException("Couldn't write during dropping map with keys and values");
                }
            }
            keyAndValueMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();

        try {
            checkStorageNotClosed();
            return presenceSet.contains(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.readLock().lock();

        try {
            checkStorageNotClosed();
            if (exists(key)) {
                keyAndOffsetMap.remove(key);
                presenceSet.remove(key);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();

        try {
            checkStorageNotClosed();
            return presenceSet.iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkStorageNotClosed();
            return presenceSet.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();

        isStorageOpened = false;
        try {
            dropKeyAndValueMapOnDisk();
            RandomAccessFile randAccFile = new RandomAccessFile(this.fileWithOffsetsPathname, mode);
            randAccFile.writeInt(keyAndOffsetMap.size());
            for (Map.Entry<K, Long> entry : keyAndOffsetMap.entrySet()) {
                keyStrategy.write(randAccFile, entry.getKey());
                randAccFile.writeLong(entry.getValue());
            }
            storageLock.release();
            randAccFile.close();
            randAccFileStorage.close();

        } catch (IOException e) {
            throw new IllegalStateException("Random access file wasn't created");
        } finally {
            lock.writeLock().unlock();
        }
    }
}
