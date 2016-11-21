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
import java.util.concurrent.ExecutionException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by Vlad on 26/10/2016.
 */
public class PerformanceStorage<K, V> implements KeyValueStorage<K, V> {

    private final String lockRelativeFilename = ".lock";
    private File lockFile;

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

        String lockAbsoluteFilename = directoryPath + File.separator + lockRelativeFilename;
        lockFile = new File(lockAbsoluteFilename);
        if (!lockFile.createNewFile()) {
            throw new IOException("Another process is working.");
        }

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
        keysFile.createNewFile();
        valuesFile.createNewFile();
        keysRandomAccessFile = new RandomAccessFile(keysFile, "rw");
        valuesRandomAccessFile = new RandomAccessFile(valuesFile, "rw");
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
        keysRandomAccessFile.seek(0);
        integerSerialization.write(keysRandomAccessFile, keyBuffer.size());
        for (K key: keyBuffer.keySet()) {
            keySerialization.write(keysRandomAccessFile, key);
            longSerialization.write(keysRandomAccessFile, keyBuffer.get(key));
        }
        keysRandomAccessFile.close();
        valuesRandomAccessFile.close();
    }

    void checkIfStorageIsClosed() {
        if (isStorageClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    @Override
    public V read(K key) {
        checkIfStorageIsClosed();
        try {
            V value = cache.get(key);
            return value;
        } catch (UncheckedExecutionException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        checkIfStorageIsClosed();
        return keyBuffer.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkIfStorageIsClosed();
        try {
            keyBuffer.put(key, valuesRandomAccessFile.length());
            valuesRandomAccessFile.seek(valuesRandomAccessFile.length());
            valueSerialization.write(valuesRandomAccessFile, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        checkIfStorageIsClosed();
        keyBuffer.remove(key);
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
        checkIfStorageIsClosed();
        isStorageClosed = true;
        lockFile.delete();
        save();
        checksumRandomAccessFile.setLength(0);
        checksumRandomAccessFile.seek(0);
        long keysHash = adler32Hash(keysAbsoluteFilename);
        long valuesHash = adler32Hash(valuesAbsoluteFilename);
        longSerialization.write(checksumRandomAccessFile, keysHash);
        longSerialization.write(checksumRandomAccessFile, valuesHash);
        checksumRandomAccessFile.close();
    }
}
