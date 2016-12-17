package ru.mipt.java2016.homework.g595.shakhray.task3.Storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.IntegerSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.LongSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Interface.StorageSerialization;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by Vlad on 26/10/2016.
 */
public class PerformanceStorage<K, V> implements KeyValueStorage<K, V> {

    private final String lockRelativeFilename = ".lock";

    private boolean isStorageClosed = false;

    private final IntegerSerialization integerSerialization = IntegerSerialization.getSerialization();
    private final LongSerialization longSerialization = LongSerialization.getSerialization();

    private final String keysRelativeFilename = "keys";
    private final String valuesRelativeFilename = "values";
    private final String checksumRelativeFilename = ".checksum.a32";

    private String keysAbsoluteFilename;
    private String valuesAbsoluteFilename;

    private StorageSerialization<K> keySerialization;
    private StorageSerialization<V> valueSerialization;

    private HashMap<K, Long> keyBuffer = new HashMap<>();

    private File keysFile;
    private File valuesFile;
    private RandomAccessFile keysRandomAccessFile;
    private RandomAccessFile valuesRandomAccessFile;
    private RandomAccessFile checksumRandomAccessFile;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<Long, Boolean> gaps = new HashMap<>();
    private int updateCount = 0;
    private String dirPath;

    private final LoadingCache<K, V> cache = CacheBuilder.newBuilder().maximumSize(32).build(
            new CacheLoader<K, V>() {
                @Override
                public V load(K k) throws Exception {
                    Long offset = keyBuffer.get(k);
                    valuesRandomAccessFile.seek(offset);
                    V value = valueSerialization.read(valuesRandomAccessFile);
                    return value;
                }
            }
    );

    private static long adler32Hash(String fileName) throws IOException {
        try {
            CheckedInputStream cis;
            cis = new CheckedInputStream(new FileInputStream(fileName), new Adler32());

            byte[] buf = new byte[128];
            int b = cis.read(buf);
            while (b >= 0) {
                b = cis.read(buf);
            }

            long checksum = cis.getChecksum().getValue();
            return checksum;
        } catch (IOException e) {
            throw e;
        }
    }

    private boolean checkValidity() throws IOException {
        try {
            Long expectedKeysChecksum = longSerialization.read(checksumRandomAccessFile);
            Long expectedValuesChecksum = longSerialization.read(checksumRandomAccessFile);
            Long actualKeysChecksum = adler32Hash(keysAbsoluteFilename);
            Long actualValuesChecksum = adler32Hash(valuesAbsoluteFilename);
            if (expectedKeysChecksum != actualKeysChecksum ||
                    expectedValuesChecksum != actualValuesChecksum) {
                return true;
            }
            return false;
        } catch (IOException e) {
            throw e;
        }


    }

    public PerformanceStorage(String directoryPath, StorageSerialization<K> passedKeySerialization,
                              StorageSerialization<V> passedValueSerialization) throws IOException {

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IOException("Directory not found.");
        }
        dirPath = directoryPath;
        String lockAbsoluteFilename = directoryPath + File.separator + lockRelativeFilename;

        keySerialization = passedKeySerialization;
        valueSerialization = passedValueSerialization;

        keysAbsoluteFilename = directoryPath + File.separator + keysRelativeFilename;
        valuesAbsoluteFilename = directoryPath + File.separator + valuesRelativeFilename;
        String checksumAbsoluteFilename = directoryPath + File.separator + checksumRelativeFilename;

        keysFile = new File(keysAbsoluteFilename);
        valuesFile = new File(valuesAbsoluteFilename);
        File checksumFile = new File(checksumAbsoluteFilename);

        if (!checksumFile.createNewFile()) {
            checksumRandomAccessFile = new RandomAccessFile(checksumFile, "rw");
            if (!checkValidity()) {
                throw new IOException("Corrupted storage.");
            }
            loadData();
        } else {
            checksumRandomAccessFile = new RandomAccessFile(checksumFile, "rw");
            createNewStorage();
        }
    }

    private void createNewStorage() throws IOException {
        lock.writeLock().lock();
        keysFile.createNewFile();
        valuesFile.createNewFile();
        keysRandomAccessFile = new RandomAccessFile(keysFile, "rw");
        valuesRandomAccessFile = new RandomAccessFile(valuesFile, "rw");
        lock.writeLock().unlock();
    }

    private void loadData() throws IOException {
        keyBuffer.clear();
        cache.cleanUp();

        keysRandomAccessFile = new RandomAccessFile(keysFile, "rw");
        valuesRandomAccessFile = new RandomAccessFile(valuesFile, "rw");

        int size = integerSerialization.read(keysRandomAccessFile);
        for (int i = 0; i < size; i++) {
            K key = keySerialization.read(keysRandomAccessFile);
            Long offset = longSerialization.read(keysRandomAccessFile);
            keyBuffer.put(key, offset);
        }
    }

    private void save() throws IOException {
        lock.writeLock().lock();
        keysRandomAccessFile.seek(0);
        integerSerialization.write(keysRandomAccessFile, keyBuffer.size());
        for (K key: keyBuffer.keySet()) {
            keySerialization.write(keysRandomAccessFile, key);
            longSerialization.write(keysRandomAccessFile, keyBuffer.get(key));
        }
        keysRandomAccessFile.close();
        valuesRandomAccessFile.close();
        lock.writeLock().unlock();
    }

    void checkIfStorageIsClosed() {
        if (isStorageClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    @Override
    public V read(K key) {
        lock.writeLock().lock();
        checkIfStorageIsClosed();
        try {
            V value = cache.get(key);
            return value;
        } catch (UncheckedExecutionException | ExecutionException e) {
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        checkIfStorageIsClosed();
        return keyBuffer.keySet().contains(key);
    }

    private void chechCompressionStatus() {
        updateCount += 1;
        if (updateCount > 5000) {
//            update();
            updateCount = 0;
        }
    }

    private void update() {
        File newFile = new File(dirPath + File.separator + "buffer");
        try (RandomAccessFile newStorage = new RandomAccessFile(newFile, "rw")) {
            newStorage.seek(0);

            int initialOffset = 0;
            valuesRandomAccessFile.seek(initialOffset);
            while (true) {
                Long offset = valuesRandomAccessFile.getFilePointer();
                if (offset >= valuesRandomAccessFile.length()) {
                    break;
                }
                K key = keySerialization.read(valuesRandomAccessFile);
                V value = valueSerialization.read(valuesRandomAccessFile);
                if (gaps.keySet().contains(offset)) {
                    continue;
                }
                keyBuffer.put(key, newStorage.getFilePointer());
                keySerialization.write(newStorage, key);
                valueSerialization.write(newStorage, value);
            }
            valuesRandomAccessFile.close();
            newFile.renameTo(new File(valuesAbsoluteFilename));
            valuesRandomAccessFile = new RandomAccessFile(newFile, "rw");

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        checkIfStorageIsClosed();
        chechCompressionStatus();
        try {
            keyBuffer.put(key, valuesRandomAccessFile.length());
            valuesRandomAccessFile.seek(valuesRandomAccessFile.length());
            valueSerialization.write(valuesRandomAccessFile, value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        checkIfStorageIsClosed();
        if (!keyBuffer.keySet().contains(key)) {
            return;
        }
        lock.writeLock().lock();
        chechCompressionStatus();
        gaps.put(keyBuffer.get(key), true);
        keyBuffer.remove(key);
        lock.writeLock().unlock();
    }

    @Override
    public Iterator<K> readKeys() {
        checkIfStorageIsClosed();
        return keyBuffer.keySet().iterator();
    }

    @Override
    public int size() {
        checkIfStorageIsClosed();
        return keyBuffer.keySet().size();
    }

    @Override
    public void close() throws IOException {
        isStorageClosed = true;
        lock.writeLock().lock();
        chechCompressionStatus();
        lock.writeLock().unlock();
        save();
        keyBuffer.clear();
        checksumRandomAccessFile.setLength(0);
        checksumRandomAccessFile.seek(0);
        long keysHash = adler32Hash(keysAbsoluteFilename);
        long valuesHash = adler32Hash(valuesAbsoluteFilename);
        longSerialization.write(checksumRandomAccessFile, keysHash);
        longSerialization.write(checksumRandomAccessFile, valuesHash);
        checksumRandomAccessFile.close();
    }
}
