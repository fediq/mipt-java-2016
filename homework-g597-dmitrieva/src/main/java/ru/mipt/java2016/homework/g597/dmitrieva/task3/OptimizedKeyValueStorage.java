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

    private static final int MAX_SIZE_OF_KEY_AND_VALUE_MAP = 100;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private HashMap<K, Long> keyAndOffsetMap;
    private Map<K, V> keyAndValueMap;
    private HashSet<K> presenceSet;
    private RandomAccessFile randAccFileStorage;
    private RandomAccessFile randAccFileWithOffsets;

    private SerializationStrategy<K> keyStrategy;
    private SerializationStrategy<V> valueStrategy;

    private String fileWithOffsetsPathname;
    private String fileStoragePathname;
    private String fileClearedStoragePathname;
    private final String mode = "rw"; // По умолчанию выставили чтение/запись
    private boolean isStorageOpened = true;
    private int garbageCounter;
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
        fileClearedStoragePathname = path + File.separator + "cleared_storage_tmp.txt";

        keyAndOffsetMap = new HashMap<K, Long>();
        keyAndValueMap = new HashMap<K, V>();
        presenceSet = new HashSet<K>();

        try {
            File fileWithOffsets = new File(fileWithOffsetsPathname);
            File fileStorage = new File(fileStoragePathname);
            if (fileWithOffsets.createNewFile()) {
                randAccFileWithOffsets = new RandomAccessFile(fileWithOffsets, mode);
                if (fileStorage.createNewFile()) {
                    randAccFileStorage = new RandomAccessFile(fileStorage, mode);
                }
                try {
                    storageLock = randAccFileWithOffsets.getChannel().lock();
                } catch (OverlappingFileLockException e) {
                    throw new IllegalStateException("Storage is already being used");
                }
                try {
                    garbageCounter = 0;
                    randAccFileWithOffsets.writeInt(0);
                    randAccFileWithOffsets.writeInt(0);
                } catch (IOException e) {
                    throw new IOException("Couldn't write during initialization of files");
                }
                storageLock.release();
                randAccFileWithOffsets.close();
            } else {
                randAccFileWithOffsets = new RandomAccessFile(fileWithOffsets, mode);
                randAccFileStorage = new RandomAccessFile(fileStorage, mode);

                try {
                    storageLock = randAccFileWithOffsets.getChannel().lock();
                } catch (OverlappingFileLockException e) {
                    throw new IllegalStateException("Storage is already being used");
                }
                int sizeOfKeyAndOffsetMap = randAccFileWithOffsets.readInt();
                garbageCounter = randAccFileWithOffsets.readInt();
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
                storageLock.release();
                randAccFileWithOffsets.close();
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
                    if (keyAndOffsetMap.remove(entry.getKey()) != null) {
                        garbageCounter++;
                    }
                    continue;
                }
                if (keyAndOffsetMap.keySet().contains(entry.getKey())) {
                    garbageCounter++;
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
                    if (keyAndOffsetMap.size() == garbageCounter) {
                        removeGarbage();
                    }
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
                keyAndValueMap.put(key, null);
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


    private void removeGarbage() {
        File clearedStorage = new File(fileClearedStoragePathname);
        try {
            if (!clearedStorage.createNewFile()) {
                throw new IllegalStateException("Couldn't create file during removing garbage");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Didn't get the length of random access file during removing garbage");
        }

        try (RandomAccessFile randAccFileClearedStorage = new RandomAccessFile(clearedStorage, mode)) {
            for (Map.Entry<K, Long> iterator : keyAndOffsetMap.entrySet()) {
                if (iterator.getValue() != null) {
                    randAccFileStorage.seek(iterator.getValue());
                    iterator.setValue(randAccFileClearedStorage.length());
                    valueStrategy.write(randAccFileClearedStorage, valueStrategy.read(randAccFileStorage));
                }
            }
            randAccFileClearedStorage.close();
            File oldStorage = new File(fileStoragePathname);
            if (!oldStorage.delete()) {
                throw new IllegalStateException("Can't delete old storage");
            }
            if (!clearedStorage.renameTo(oldStorage)) {
                throw new IllegalStateException("Can't rename file");
            }
            randAccFileStorage = new RandomAccessFile(oldStorage, mode);

        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read/write during removing garbage");
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();

        isStorageOpened = false;
        try {
            dropKeyAndValueMapOnDisk();
            if (keyAndOffsetMap.size() == garbageCounter) {
                removeGarbage();
            }
            randAccFileWithOffsets = new RandomAccessFile(this.fileWithOffsetsPathname, mode);
            try {
                storageLock = randAccFileWithOffsets.getChannel().lock();
            } catch (OverlappingFileLockException e) {
                throw new IllegalStateException("Storage is already being used");
            }
            randAccFileWithOffsets.writeInt(keyAndOffsetMap.size());
            randAccFileWithOffsets.writeInt(garbageCounter);
            for (Map.Entry<K, Long> entry : keyAndOffsetMap.entrySet()) {
                keyStrategy.write(randAccFileWithOffsets, entry.getKey());
                randAccFileWithOffsets.writeLong(entry.getValue());
            }
            storageLock.release();
            randAccFileWithOffsets.close();
            randAccFileStorage.close();
        } catch (IOException e) {
            throw new IllegalStateException("Random access file wasn't created");
        } finally {
            lock.writeLock().unlock();
        }
    }
}
