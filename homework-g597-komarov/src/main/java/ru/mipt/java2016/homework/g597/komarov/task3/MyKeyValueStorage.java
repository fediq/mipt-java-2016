package ru.mipt.java2016.homework.g597.komarov.task3;

/**
 * Created by mikhail on 17.11.16.
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import  ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.komarov.task2.Serializer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final String pathToStorage;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private final ReentrantReadWriteLock lock;
    private final File flag;
    private final RandomAccessFile keyOffsetTable;
    private RandomAccessFile valueTable;
    private Map<K, Long> dataBase;
    private int deletedCount;
    private Map<K, V> written;


    public MyKeyValueStorage(String path, Serializer<K> keySerializerArg,
                             Serializer<V> valueSerializerArg) throws IOException {
        flag = Paths.get(path, "flag").toFile();
        if (!flag.createNewFile()) {
            throw new RuntimeException("File has already been opened");
        }

        lock = new ReentrantReadWriteLock();
        pathToStorage = path;
        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        dataBase = new HashMap<>();
        written = new HashMap<>();
        deletedCount = 0;
        File pathToFile = Paths.get(path, "storage.db").toFile();

        try {
            valueTable = new RandomAccessFile(pathToFile, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("File not found");
        }

        pathToFile = Paths.get(path, "index.db").toFile();
        try {
            keyOffsetTable = new RandomAccessFile(pathToFile, "rw");
            dataBase = readMapFromFile();
        } catch (FileNotFoundException e) {
            throw new IOException("File not found");
        }
    }

    @Override
    public V read(K key) {
        lock.writeLock().lock();
        try {
            checkState();
            if (!dataBase.containsKey(key)) {
                return null;
            }
            long offset = dataBase.get(key);
            if (offset < 0) {
                return written.get(key);
            }
            try {
                valueTable.seek(offset);
                return valueSerializer.read(valueTable);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            checkState();
            return dataBase.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkState();
            dataBase.put(key, (long) -1);
            written.put(key, value);
            if (written.size() >= 100) {
                try {
                    merge();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            checkState();
            if (exists(key)) {
                deletedCount++;
                dataBase.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            checkState();
            return dataBase.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkState();
            return dataBase.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        checkState();
        lock.writeLock().lock();
        try {
            if (written.size() != 0) {
                merge();
            }
            if (deletedCount != 0) {
                rewriteFile();
            }
            dataBase = null;
            written = null;
            deletedCount = 0;
            valueTable.close();
            keyOffsetTable.close();
            flag.delete();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void checkState() {
        if (dataBase == null) {
            throw new RuntimeException("Already closed");
        }
    }

    private Map<K, Long> readMapFromFile() throws IOException {
        Map<K, Long> bufMap = new HashMap<>();
        K key;
        long offset;
        keyOffsetTable.seek(0);
        while (keyOffsetTable.getFilePointer() < keyOffsetTable.length()) {
            key = keySerializer.read(keyOffsetTable);
            offset = keyOffsetTable.readLong();
            bufMap.put(key, offset);
        }
        return bufMap;
    }

    private void merge() throws IOException {
        if (deletedCount >= 100) {
            try {
                rewriteFile();
                deletedCount = 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        long offset = valueTable.length();
        valueTable.seek(offset);
        keyOffsetTable.seek(keyOffsetTable.length());
        for (Map.Entry<K, V> entry : written.entrySet()) {
            keySerializer.write(keyOffsetTable, entry.getKey());
            keyOffsetTable.writeLong(offset);
            dataBase.remove(entry.getKey());
            dataBase.put(entry.getKey(), offset);
            valueSerializer.write(valueTable, entry.getValue());
            offset = valueTable.length();
        }
        written.clear();
    }

    private void rewriteFile() throws IOException {
        keyOffsetTable.setLength(0);
        keyOffsetTable.seek(0);

        File pathToFile = Paths.get(pathToStorage, "storageCopy.db").toFile();
        try (RandomAccessFile bufFile = new RandomAccessFile(pathToFile, "rw")) {
            bufFile.seek(0);
            long offset = 0;
            V bufValue;

            for (Map.Entry<K, Long> entry : dataBase.entrySet()) {
                if (entry.getValue() >= 0) {
                    keySerializer.write(keyOffsetTable, entry.getKey());
                    keyOffsetTable.writeLong(offset);
                    valueTable.seek(entry.getValue());
                    bufValue = valueSerializer.read(valueTable);
                    valueSerializer.write(bufFile, bufValue);
                    offset += bufFile.length();
                }
            }

            valueTable.close();
            bufFile.close();
            Files.move(Paths.get(pathToStorage + File.separator + "storageCopy.db"),
                    Paths.get(pathToStorage + File.separator + "storage.db"),
                    REPLACE_EXISTING);
            valueTable = bufFile;
        }
    }
}
