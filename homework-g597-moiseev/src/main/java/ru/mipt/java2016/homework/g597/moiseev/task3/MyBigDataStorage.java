package ru.mipt.java2016.homework.g597.moiseev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.moiseev.task2.IntegerSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.SerializationStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Дисковое хранилище.
 *
 * @author Fedor Moiseev
 * @since 19.11.16
 */

public class MyBigDataStorage<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private static final int MAX_MEM_TABLE_SIZE = 4000;
    private static final double MAX_DELETED_PROPORTION = 0.2;

    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;
    private final SerializationStrategy<Long> offsetSerializationStrategy = LongSerializationStrategy.getInstance();
    private final SerializationStrategy<Integer> integerSerializationStrategy = IntegerSerializationStrategy.getInstance();
    private String name;
    private String path;
    private RandomAccessFile valuesFile;
    private RandomAccessFile keysFile;
    private File lockFile;
    private HashMap<K, V> memTable;
    private HashMap<K, Long> offsets;
    private int numberOfDeletedElements;
    private int numberOfDuplicates;
    private boolean isOptimising;
    private boolean isDumping;
    private ReentrantLock readLock = new ReentrantLock();
    private ReentrantLock writeLock = new ReentrantLock();
    private ReentrantLock duplicateLock = new ReentrantLock();

    public MyBigDataStorage(String path, String name, SerializationStrategy<K> keySerializationStrategy,
                             SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        this.path = path;
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory doesn't exist");
        }


        String lockPath = path + File.separator + name + ".lock";
        lockFile = new File(lockPath);
        if (!lockFile.createNewFile()) {
            throw new IOException("Database is already open");
        }

        memTable = new HashMap<>();
        offsets = new HashMap<>();
        this.name = name;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        String valuesPath = path + File.separator + this.name + "_values";
        File values = new File(valuesPath);

        String keysPath = path + File.separator + this.name + "_keys";
        File keys = new File(keysPath);

        valuesFile = new RandomAccessFile(values, "rw");
        keysFile = new RandomAccessFile(keys, "rw");

        numberOfDeletedElements = 0;
        numberOfDuplicates = 0;
        isDumping = false;
        isOptimising = true;

        if (!values.createNewFile() || !keys.createNewFile()) {
            loadFromFiles();
        }
    }

    private void loadFromFiles() throws IOException {
        keysFile.seek(0);
        memTable.clear();

        long fileLength = keysFile.length();

        numberOfDeletedElements = integerSerializationStrategy.read(keysFile);

        while (keysFile.getFilePointer() < fileLength) {
            K key;
            long offset;
            key = keySerializationStrategy.read(keysFile);
            offset = offsetSerializationStrategy.read(keysFile);
            if (offsets.containsKey(key)) {
                throw new IOException("Duplicate keys");
            } else {
                offsets.put(key, offset);
            }
        }
    }

    @Override
    public V read(K key) {
        readLock.lock();
        V result = null;
        try {
            if(offsets.containsKey(key)) {
                if (memTable.containsKey(key)) {
                    result = memTable.get(key);
                } else {
                    long offset = offsets.get(key);
                    valuesFile.seek(offset);
                    result = valueSerializationStrategy.read(valuesFile);
                }
            }
        } catch (IOException e) {
            result = null;
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public boolean exists(K key) {
        writeLock.lock();
        boolean result;
        try {
            result = offsets.containsKey(key) || memTable.containsKey(key);
        } finally {
            writeLock.unlock();
        }
        return result;
    }

    @Override
    public void write(K key, V value) {
        readLock.lock();
        writeLock.lock();
        try {
            if(offsets.containsKey(key) && !memTable.containsKey(key)) {
                numberOfDeletedElements++;
            }

            memTable.put(key, value);
            offsets.remove(key);

            checkSizeAndDumpOrOptimize();

        } finally {
            readLock.unlock();
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        readLock.lock();
        writeLock.lock();
        try {
            if (offsets.containsKey(key)) {
                numberOfDeletedElements++;
            }
            offsets.remove(key);
            memTable.remove(key);
        } finally {
            readLock.unlock();
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        int result = 0;
        writeLock.lock();
        duplicateLock.lock();
        try {
            result = offsets.size() + memTable.size() - numberOfDuplicates;
        } finally {
            writeLock.unlock();
            duplicateLock.unlock();
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        readLock.lock();
        writeLock.lock();
        try {
            dumpMemTable();

            keysFile.seek(0);
            keysFile.setLength(0);

            integerSerializationStrategy.write(keysFile, numberOfDeletedElements);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializationStrategy.write(keysFile, entry.getKey());
                offsetSerializationStrategy.write(keysFile, entry.getValue());
            }
        } finally {
            keysFile.close();
            valuesFile.close();
            Files.delete(lockFile.toPath());
            readLock.unlock();
            writeLock.unlock();
        }
    }

    private void checkSizeAndDumpOrOptimize() {
        if (!isDumping && !isOptimising) {
            if (numberOfDeletedElements / size() > MAX_DELETED_PROPORTION) {
                Thread thread = new Thread(() -> optimizeMemory());
                thread.start();
            } else if (memTable.size() > MAX_MEM_TABLE_SIZE) {
                Thread thread = new Thread(() -> dumpMemTable());
                thread.start();
            }
        }
    }

    private void dumpMemTable() {
        writeLock.lock();
        isDumping = true;
        writeLock.unlock();
        try {
            long offset = valuesFile.length();
            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                duplicateLock.lock();
                try {
                    offsets.put(entry.getKey(), offset);
                    numberOfDuplicates++;
                } finally {
                    duplicateLock.unlock();
                }
                valueSerializationStrategy.write(valuesFile, entry.getValue());
                offset = valuesFile.length();
                memTable.clear();
                duplicateLock.lock();
                numberOfDuplicates = 0;
                duplicateLock.unlock();
                isDumping = false;
            }
        } catch (IOException e) {

        }
    }

    private void optimizeMemory() {
        dumpMemTable();
        writeLock.lock();
        isOptimising = true;
        writeLock.unlock();

        try {
            HashMap<K, Long> newOffsets = new HashMap<>();
            String newValuesPath = path + File.separator + "new_" + this.name + "_values";
            String valuesPath = path + File.separator + this.name + "_values";
            File newValues = new File(newValuesPath);
            File values = new File(valuesPath);

            newValues.createNewFile();

            RandomAccessFile newValuesFile = new RandomAccessFile(newValues, "rw");
            newValuesFile.seek(0);
            newValuesFile.setLength(0);

            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                readLock.lock();
                valuesFile.seek(entry.getValue());
                V value = valueSerializationStrategy.read(valuesFile);
                readLock.unlock();

                newOffsets.put(entry.getKey(), newValuesFile.length());
                valueSerializationStrategy.write(newValuesFile, value);
            }

            readLock.lock();
            numberOfDeletedElements = 0;
            offsets = newOffsets;
            valuesFile = newValuesFile;
            Files.delete(values.toPath());
            while (!newValues.renameTo(values));
            readLock.unlock();

        }
        catch (IOException e) {

        }
    }
}
