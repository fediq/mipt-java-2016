package ru.mipt.java2016.homework.g594.rubanenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * Created by king on 17.11.16.
 */

public class FastKeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private static final String STORAGE_NAME = "storage.db";
    private static final String REFERENCE_FILE_NAME = "reference.db";
    private static final String LOCK_FILE_NAME = "lock.db";
    private String fileDirectory;
    private File lockFile;
    private FastStorage storage;
    private FastReferenceFile referenceFile;
    private FastKeyValueStorageSerializer<K> keySerializer;
    private FastKeyValueStorageSerializer<V> valueSerializer;
    private boolean isOpened = false;
    private long storageLength;
    private long recordLength;
    private long deletedField = 0;
    private final HashMap<K, Long> offsetTable  = new HashMap<K, Long>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock writeLock = readWriteLock.writeLock();
    private Lock readLock = readWriteLock.readLock();
    private boolean updated = false;
    private LinkedHashMap<K, V> cache;
    private static final int CACHE_SIZE = 100;

    public FastKeyValueStorageImpl(String fileDirectoryInit, FastKeyValueStorageSerializer keySerializerInit,
                                   FastKeyValueStorageSerializer valueSerializerInit) throws IOException {
        isOpened = true;
        keySerializer = keySerializerInit;
        valueSerializer = valueSerializerInit;
        fileDirectory = fileDirectoryInit;
        referenceFile = new FastReferenceFile(fileDirectory, REFERENCE_FILE_NAME);
        storage = new FastStorage(fileDirectory, STORAGE_NAME);
        storageLength = storage.getLength();
        recordLength = storageLength - 1;
        cache = new LinkedHashMap<K, V>();
        String lockFilePath = fileDirectory + File.separator + LOCK_FILE_NAME;
        lockFile = new File(lockFilePath);
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch (Exception e) {
                throw new IOException("Failed to create lock file");
            }
        } else {
            throw new IOException("Somebody is working w/ db right now.");
        }
        startWorkWithOffsetTable();
    }

    /* ! First we look for key in cache, than we try to find it in the storage */
    @Override
    public V read(K key) {
        V value;
        writeLock.lock();
        try {
            checkIfClosed();
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            if (!exists(key)) {
                value = null;
            } else {
                value = readValue(key);
            }
        } catch (IOException e) {
            value = null;
        } finally {
            writeLock.unlock();
        }
        return value;
    }

    /* ! First we look for key in cache, than we try to find it in the storage */
    @Override
    public boolean exists(K key) {
        readLock.lock();
        try {
            checkIfClosed();
            if (cache.containsKey(key)) {
                return true;
            }
            return offsetTable.keySet().contains(key);
        } finally {
            readLock.unlock();
        }
    }

    /* ! First we look for key in cache, if we succeed we upgrade the value
    * otherwise we put key - value to cache; we put key - value to storage */
    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkIfClosed();
            if (cache.containsKey(key)) {
                cache.put(key, value);
                balanceCache();
            } else {
                writeField(key, value);
                cache.put(key, value);
                balanceCache();
                updated = true;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error during writing");
        } finally {
            writeLock.unlock();
        }
    }

    /* ! delete key - value from storage and from cache if needed */
    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkIfClosed();
            if (cache.containsKey(key)) {
                cache.remove(key);
            }
            offsetTable.remove(key);
            ++deletedField;
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        readLock.lock();
        try {
            checkIfClosed();
            return offsetTable.keySet().iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            checkIfClosed();
            return offsetTable.size();
        } finally {
            readLock.unlock();
        }
    }

    /* ! close files and write changes to the disk */
    @Override
    public void close() throws IOException {
        writeLock.lock();
        try {
            checkIfClosed();
            updateStorageFile();
            storage.close();
            if (updated) {
                endWorkWithOffsetTable();
                updated = false;
            }
            if (lockFile.exists()) {
                try {
                    lockFile.delete();
                } catch (Exception e) {
                    throw new IOException("Failed to delete lock file");
                }
            }
            referenceFile.close();
            isOpened = false;
        } finally {
            writeLock.unlock();
        }
    }

    private void startWorkWithOffsetTable() throws IOException {
        if (!referenceFile.checkIsEmpty()) {
            int counter = referenceFile.readKey();
            while (counter > 0) {
                K key = keySerializer.deserializeFromStream(referenceFile.readBytes(counter));
                long offset = referenceFile.readOffset();
                offsetTable.put(key, offset);
                counter = referenceFile.readKey();
            }
        }
    }

    private void endWorkWithOffsetTable() throws IOException {
        referenceFile.makeEmpty();
        for (HashMap.Entry<K, Long> iterator : offsetTable.entrySet()) {
            referenceFile.writeSize(keySerializer.serializeSize(iterator.getKey()));
            referenceFile.writeBytes(keySerializer.serializeToStream(iterator.getKey()));
            referenceFile.writeOffset(iterator.getValue());
        }
        offsetTable.clear();
        if (lockFile.exists()) {
            try {
                lockFile.delete();
            } catch (Exception e) {
                throw new IOException("Failed to delete lock file");
            }
        }
    }

    private void checkFreeSpace(long offset) throws IOException {
        if (offset > recordLength) {
            storage.flushStream();
            recordLength = storageLength - 1;
        }
    }

    private V readValue(K key) throws IOException {
        long offset = offsetTable.get(key);
        checkFreeSpace(offset);
        int serializedKey = storage.readKey(offset);
        return valueSerializer.deserializeFromStream(storage.readBytes(serializedKey));
    }

    private void writeOffsetField(K key) throws IOException {
        offsetTable.put(key, keySerializer.serializeSize(key) + Integer.SIZE / 8 + storageLength);
    }

    private void writeKey(K key) throws IOException {
        storage.writeSizeToStream(keySerializer.serializeSize(key));
        storage.writeBytesToStream(keySerializer.serializeToStream(key));
    }

    private void writeValue(V value) throws IOException {
        storage.writeSizeToStream(valueSerializer.serializeSize(value));
        storage.writeBytesToStream(valueSerializer.serializeToStream(value));
    }

    private void updateStorageLength(K key, V value) throws IOException {
        storageLength = storageLength + Integer.SIZE / 8 + keySerializer.serializeSize(key) +
                Integer.SIZE / 8 + valueSerializer.serializeSize(value);
    }

    private void writeField(K key, V value) throws IOException {
        writeOffsetField(key);
        writeKey(key);
        writeValue(value);
        updateStorageLength(key, value);
    }

    private void updateStorageFile() throws IOException {
        if (deletedField > 4 * offsetTable.size()) {
            storage.beginUpdate();
            storageLength = 0;
            long oldFileOffset = 0;
            int keySize = storage.readKeyFromStream();
            while (keySize > 0) {
                ByteBuffer serializedKey = storage.readValueFromStream(keySize);
                K key = keySerializer.deserializeFromStream(serializedKey);
                int valueSize = storage.readKeyFromStream();
                ByteBuffer value = storage.readValueFromStream(valueSize);
                if (offsetTable.containsKey(key) && offsetTable.get(key).equals(oldFileOffset)) {
                    writeField(key, valueSerializer.deserializeFromStream(value));
                }
                oldFileOffset = oldFileOffset + Integer.SIZE / 8 + Integer.SIZE / 8 + keySize + valueSize;
                keySize = storage.readKeyFromStream();
            }
            storage.endUpdate();
        }
    }

    private void checkIfClosed() {
        if (!isOpened) {
            throw new IllegalStateException("Impossible to work with closed storage");
        }
    }

    /* ! method for controlling cache size */
    private void balanceCache() {
        if (cache.size() > CACHE_SIZE) {
            Iterator<Map.Entry<K, V>> iterator = cache.entrySet().iterator();
            iterator.next();
            iterator.remove();
        }
    }
}
