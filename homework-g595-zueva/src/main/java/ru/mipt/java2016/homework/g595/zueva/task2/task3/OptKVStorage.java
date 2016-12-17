package ru.mipt.java2016.homework.g595.zueva.task2.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * find in the cache firstly.
 */
public class OptKVStorage<K,V> implements KeyValueStorage<K,V> {
    private static final String STORAGE_NAME = "somestorage.db";
    private String FileIn;
    private final HashMap<K, Long> offsetTable  = new HashMap<K, Long>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock writeLock = readWriteLock.writeLock();
    private Lock readLock = readWriteLock.readLock();
    private static final String REFERENCE_FILE_NAME = "ref.db";
    private static final String LOCK_FILE_NAME = "locking.db";
    private File LockingMyFile;
    private MyOptKVStorage storage;
    private FRFile RefFile;
    private OptKVStorageSerializer<K> KeySerializer;
    private OptKVStorageSerializer<V> ValueSerializer;
    private boolean ifOpen = false;
    private long StorSize;
    private long recordLength;
    private long deletedField = 0;
    private boolean updated = false;
    private LinkedHashMap<K, V> cache;
    private static final int CACHE_SIZE = 1000;

    public OptKVStorage(String fileDirectoryInit, OptKVStorageSerializer keySerializerInit,
                        OptKVStorageSerializer valueSerializerInit) throws IOException {
        ifOpen = true;
        KeySerializer = keySerializerInit;
        ValueSerializer = valueSerializerInit;
        FileIn = fileDirectoryInit;
        RefFile = new FRFile(FileIn, REFERENCE_FILE_NAME);
        storage = new MyOptKVStorage(FileIn, STORAGE_NAME);
        StorSize = storage.getLength();
        recordLength = StorSize - 1;
        cache = new LinkedHashMap<K, V>();
        String lockFilePath = FileIn + File.separator + LOCK_FILE_NAME;
        LockingMyFile = new File(lockFilePath);
        if (!LockingMyFile.exists()) {
            try {
                LockingMyFile.createNewFile();
            } catch (Exception e) {
                throw new IOException("Failed to create lock file");
            }
        } else {
            throw new IOException("Somebody is working w/ db right now.");
        }
        startWorkWithOffsetTable();
    }
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
+    * otherwise we put key - value to cache; we put key - value to storage */
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
            if (LockingMyFile.exists()) {
                try {
                    LockingMyFile.delete();
                } catch (Exception e) {
                    throw new IOException("Failed to delete lock file");
                }
            }
            RefFile.close();
            ifOpen = false;
        } finally {
            writeLock.unlock();
        }
    }

    private void startWorkWithOffsetTable() throws IOException {
        if (!RefFile.checkIsEmpty()) {
            int counter = RefFile.readKey();
            while (counter > 0) {
                K key = KeySerializer.desrlzFrStr(RefFile.byteReading(counter));
                long offset = RefFile.ReadFileOffst();
                offsetTable.put(key, offset);
                counter = RefFile.readKey();
            }
        }
    }

    private void endWorkWithOffsetTable() throws IOException {
        RefFile.Clean();
        for (HashMap.Entry<K, Long> iterator : offsetTable.entrySet()) {
            RefFile.SizeWr(KeySerializer.SrlzSize(iterator.getKey()));
            RefFile.writeBytes(KeySerializer.srlzToStr(iterator.getKey()));
            RefFile.writeFileOffst(iterator.getValue());
        }
        offsetTable.clear();
        if (LockingMyFile.exists()) {
            try {
                LockingMyFile.delete();
            } catch (Exception e) {
                throw new IOException("Failed to delete lock file");
            }
        }
    }

    private void checkFreeSpace(long offset) throws IOException {
        if (offset > recordLength) {
            storage.flushStream();
            recordLength = StorSize - 1;
        }
    }

    private V readValue(K key) throws IOException {
        long offset = offsetTable.get(key);
        checkFreeSpace(offset);
        int serializedKey = storage.readKey(offset);
        return ValueSerializer.desrlzFrStr(storage.readBytes(serializedKey));
    }

    private void writeOffsetField(K key) throws IOException {
        offsetTable.put(key, KeySerializer.SrlzSize(key) + Integer.SIZE / 8 + StorSize);
    }

    private void writeKey(K key) throws IOException {
        storage.writeSizeToStream(KeySerializer.SrlzSize(key));
        storage.writeBytesToStream(KeySerializer.srlzToStr(key));
    }

    private void writeValue(V value) throws IOException {
        storage.writeSizeToStream(ValueSerializer.SrlzSize(value));
        storage.writeBytesToStream(ValueSerializer.srlzToStr(value));
    }

    private void updateStorageLength(K key, V value) throws IOException {
        StorSize = StorSize + Integer.SIZE / 8 + KeySerializer.SrlzSize(key) +
                Integer.SIZE / 8 + ValueSerializer.SrlzSize(value);
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
            StorSize = 0;
            long oldFileOffset = 0;
            int keySize = storage.readKeyFromStream();
            while (keySize > 0) {
                ByteBuffer serializedKey = storage.readValueFromStream(keySize);
                K key = KeySerializer.desrlzFrStr(serializedKey);
                int valueSize = storage.readKeyFromStream();
                ByteBuffer value = storage.readValueFromStream(valueSize);
                if (offsetTable.containsKey(key) && offsetTable.get(key).equals(oldFileOffset)) {
                    writeField(key, ValueSerializer.desrlzFrStr(value));
                }
                oldFileOffset = oldFileOffset + Integer.SIZE / 8 + Integer.SIZE / 8 + keySize + valueSize;
                keySize = storage.readKeyFromStream();
            }
            storage.endUpdate();
        }
    }

    private void checkIfClosed() {
        if (!ifOpen) {
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




