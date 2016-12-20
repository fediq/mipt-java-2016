package ru.mipt.java2016.homework.g596.pockonechny.task3;

/**
 * Created by celidos on 19.11.16.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class AdvancedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_STORAGE_OFFSET = 2000000000;
    private static final int MAX_READING_BLOCK_MEMORY_SIZE = 1;
    private static final int MAX_WRITING_BLOCK_MEMORY_SIZE = 100;
    private static final int MAX_CHANGES_COUNTER = 20000;

    private static final String DEFAULT_STORAGE_VALUES_FILENAME = "storage.kvs";
    private static final String DEFAULT_STORAGE_OFFSET_FILENAME = "storage_offset.kvs";
    private static final String DEFAULT_STORAGE_VALUES_COPY_FILENAME = "~storage.kvs";
    private static final String DEFAULT_STORAGE_OFFSET_COPY_FILENAME = "~storage_offset.kvs";

    // FIELDS -------------------------

    private String offsetFilename;
    private String valuesFilename;
    private String workspaceDir;
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;
    private String contentType;
    private boolean isStreamingNow;
    private RandomAccessFile storageDevice;

    private Map<K, V> cachedReadingBlockMap;    // cached block for optimization of repetetive values reading
    private Map<K, V> cachedWritingBlockMap;    // cached block for flushing by blocks
    private Map<K, Long> offsetFullMap;         // map of the offsets in file
    private Set<K> existKeysFullMap;            // keys represented in temp buffer

    private Integer changesCounter;

    private ReadWriteLock lock;
    private Lock readlock;
    private Lock writelock;

    // STUFF --------------------------

    private void checkPathExistance(String path) throws IllegalStateException {
        File pathref = new File(path);
        if (!pathref.exists() || !pathref.isDirectory() || pathref.exists() == Files.notExists(pathref.toPath())) {
            throw new IllegalStateException("Path is not available / Undefined path state");
        }
    }

    private void safeCreateFile(File file) throws IllegalStateException {
        try {
            if (!file.createNewFile()) {
                throw new IllegalStateException("Cannot create file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean checkTransferFileIsOk(File transferFile) throws IllegalStateException {
        try {
            if (storageDevice.length() <= MAX_STORAGE_OFFSET) {
                return false;
            }
            if (!transferFile.createNewFile()) {
                throw new IllegalStateException("Cannot create file");
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Transfer file operation error");
        }
        return true;
    }

    private void createStorage(File offsetFile, File valuesFile) throws IllegalStateException {
        try {
            if (!offsetFile.createNewFile()) {
                throw new IllegalStateException("Cannot create offset file");
            }
            if (!valuesFile.createNewFile()) {
                throw new IllegalStateException("Cannot create values file");
            }
            storageDevice = new RandomAccessFile(valuesFile, "rw");
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Invalid file operation");
        }
    }

    private void readDataFromStorage(File offsetFile, File valuesFile) {
        try (DataInputStream readingDevice = new DataInputStream(
                new BufferedInputStream(new FileInputStream(offsetFile)))) {

            storageDevice = new RandomAccessFile(valuesFile, "rw");

            String infileContentType = readingDevice.readUTF();
            if (!infileContentType.equals(contentType)) {
                throw new IllegalStateException("Invalid data lol format in storage");
            }

            int nKeys = readingDevice.readInt();
            SerializationStrategy<Long> offsetReader = new LongSerialization();
            for (int i = 0; i < nKeys; ++i) {
                K key = this.keySerialization.read(readingDevice);
                Long offset = offsetReader.read(readingDevice);
                existKeysFullMap.add(key);
                offsetFullMap.put(key, offset);
            }
        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("Invalid data format in storage");
        }
    }

    private void flushWritingBlock() {
        try {
            storageDevice.seek(storageDevice.length());
            for (Map.Entry<K, V> nextEntry : cachedWritingBlockMap.entrySet()) {
                if (nextEntry.getValue() != null) {
                    offsetFullMap.put(nextEntry.getKey(), storageDevice.getFilePointer());
                    valueSerialization.write(storageDevice, nextEntry.getValue());
                } else {
                    offsetFullMap.put(nextEntry.getKey(), (long) -1);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        cachedWritingBlockMap.clear();
    }

    private void refreshReadingBlock() {
        if (cachedReadingBlockMap.size() > MAX_READING_BLOCK_MEMORY_SIZE) {
            Iterator<Map.Entry<K, V>> i = cachedReadingBlockMap.entrySet().iterator();
            i.next();
            i.remove();
        }
    }

    private void refreshWritingBlock() {
        if (cachedWritingBlockMap.size() <= MAX_WRITING_BLOCK_MEMORY_SIZE) {
            return;
        }
        flushWritingBlock();
    }

    private void refreshStorage() throws IOException {
        ++changesCounter;

        if (changesCounter > MAX_CHANGES_COUNTER) {
            changesCounter = 0;
            updateValuesInStorage();
        }
    }

    private void checkStorageAvailability() {
        if (!isStreamingNow) {
            throw new IllegalStateException("Operation refer to closed storage");
        }
    }

    private void transferFile(RandomAccessFile writingDevice) throws IOException {
        for (Map.Entry<K, Long> iterator : offsetFullMap.entrySet()) {
            if (!iterator.getValue().equals((long) -1)) {
                storageDevice.seek(iterator.getValue());
                iterator.setValue(writingDevice.getFilePointer());
                valueSerialization.write(writingDevice, valueSerialization.read(storageDevice));
            }
        }
    }

    private void updateOffsetsInStorage() throws IOException {
        File newStorage = new File(workspaceDir + File.separator + DEFAULT_STORAGE_OFFSET_COPY_FILENAME);
        safeCreateFile(newStorage);

        DataOutputStream finishWriter;
        try {
            finishWriter = new DataOutputStream(new
                    BufferedOutputStream(new FileOutputStream(newStorage)));
            commitOffsetsToFile(finishWriter);
        } catch (IOException e) {
            throw new IOException(e);
        }

        finishWriter.flush();
        storageDevice.close();

        File oldOffset = new File(offsetFilename);
        if (!oldOffset.delete()) {
            throw new IllegalStateException("Cannot delete old file");
        }
        if (!newStorage.renameTo(oldOffset)) {
            throw new IllegalStateException("Cannot rename file");
        }

    }

    private void updateValuesInStorage() {
        File transfer = new File(workspaceDir + File.separator + DEFAULT_STORAGE_VALUES_COPY_FILENAME);

        if (!checkTransferFileIsOk(transfer)) {
            return;
        }

        try (RandomAccessFile transferWriter = new RandomAccessFile(transfer, "rw")) {

            transferFile(transferWriter);

            storageDevice.close();

            File oldStorage = new File(valuesFilename);

            if (!oldStorage.delete()) {
                throw new IllegalStateException("Cannot remove file");
            }

            if (!transfer.renameTo(oldStorage)) {
                throw new IllegalStateException("Cannot rename file");
            }

            storageDevice = new RandomAccessFile(transfer, "rw");
        } catch (IOException e) {
            throw new IllegalStateException("Invalid file operation");
        }
    }

    private void commitOffsetsToFile(DataOutputStream writingDevice) throws IOException {
        writingDevice.writeUTF(contentType);
        writingDevice.writeInt(existKeysFullMap.size());
        SerializationStrategy<Long> offsetWriter = new LongSerialization();
        for (Map.Entry<K, Long> iterator : offsetFullMap.entrySet()) {
            if (!iterator.getValue().equals((long) -1)) {
                keySerialization.write(writingDevice, iterator.getKey());
                offsetWriter.write(writingDevice, iterator.getValue());
            }
        }
    }

    private V tryReadFromRwCache(K key) {
        if (cachedWritingBlockMap.containsKey(key)) {
            V currentValue = cachedWritingBlockMap.get(key);
            cachedReadingBlockMap.put(key, currentValue);
            refreshReadingBlock();
            return currentValue;
        }

        if (cachedReadingBlockMap.containsKey(key)) {
            V cacheValue = cachedReadingBlockMap.remove(key);
            cachedReadingBlockMap.put(key, cacheValue);
            return cacheValue;
        }

        return null;
    }

    // Override -----------------------

    AdvancedKeyValueStorage(String path, SerializationStrategy<K> keySerialization,
                            SerializationStrategy<V> valueSerialization) throws IllegalStateException {
        isStreamingNow = true;
        offsetFilename = path + File.separator + DEFAULT_STORAGE_OFFSET_FILENAME;
        valuesFilename = path + File.separator + DEFAULT_STORAGE_VALUES_FILENAME;
        workspaceDir = path;
        cachedReadingBlockMap = new LinkedHashMap<>();
        contentType = keySerialization.getType() + "_TO_" + valueSerialization.getType();
        cachedWritingBlockMap = new HashMap<>();
        existKeysFullMap = new HashSet<>();
        offsetFullMap = new HashMap<>();

        changesCounter = 0;

        lock = new ReentrantReadWriteLock();
        readlock = lock.readLock();
        writelock = lock.writeLock();

        this.keySerialization = keySerialization;
        this.valueSerialization = valueSerialization;

        checkPathExistance(path);

        File offsetFile = new File(offsetFilename);
        File valuesFile = new File(valuesFilename);

        if (!offsetFile.exists()) {
            createStorage(offsetFile, valuesFile);
        } else {
            readDataFromStorage(offsetFile, valuesFile);
        }
    }

    @Override
    public void close() throws IOException {
        writelock.lock();
        try {
            checkStorageAvailability();

            isStreamingNow = false;

            flushWritingBlock();
            updateValuesInStorage();
            updateOffsetsInStorage();

        } finally {
            writelock.unlock();
        }
    }

    @Override
    public int size() throws IllegalStateException {
        readlock.lock();
        try {
            checkStorageAvailability();
            return existKeysFullMap.size();
        } finally {
            readlock.unlock();
        }
    }

    @Override
    public V read(K key) throws IllegalStateException {
        writelock.lock();

        try {
            checkStorageAvailability();

            V currentValue = tryReadFromRwCache(key);
            if (currentValue != null) {
                return currentValue;
            }

            if (offsetFullMap.containsKey(key)) {
                if (offsetFullMap.get(key).equals((long) -1)) {
                    cachedReadingBlockMap.put(key, null);
                    refreshReadingBlock();
                    return null;
                }
                try {
                    V value;

                    storageDevice.seek(offsetFullMap.get(key));
                    value = valueSerialization.read(storageDevice);
                    cachedReadingBlockMap.put(key, value);
                    refreshReadingBlock();

                    return value;
                } catch (IllegalStateException | IOException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                cachedReadingBlockMap.put(key, null);
                refreshReadingBlock();
                return null;
            }
        } finally {
            writelock.unlock();
        }
    }

    @Override
    public boolean exists(K key) throws IllegalStateException {
        readlock.lock();

        try {
            checkStorageAvailability();
            return existKeysFullMap.contains(key);
        } finally {
            readlock.unlock();
        }
    }

    @Override
    public void write(K key, V value) throws IllegalStateException {
        writelock.lock();
        try {
            checkStorageAvailability();

            refreshStorage();

            if (cachedReadingBlockMap.containsKey(key)) {
                cachedReadingBlockMap.put(key, value);
            }
            existKeysFullMap.add(key);
            cachedWritingBlockMap.put(key, value);

            refreshWritingBlock();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writelock.unlock();
        }
    }

    @Override
    public void delete(K key) throws IllegalStateException {
        writelock.lock();
        try {
            checkStorageAvailability();

            refreshStorage();

            if (cachedReadingBlockMap.containsKey(key)) {
                cachedReadingBlockMap.put(key, null);
            }
            existKeysFullMap.remove(key);
            cachedWritingBlockMap.put(key, null);

            refreshWritingBlock();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writelock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() throws IllegalStateException {
        readlock.lock();

        try {
            checkStorageAvailability();
            return existKeysFullMap.iterator();
        } finally {
            readlock.unlock();
        }
    }
}