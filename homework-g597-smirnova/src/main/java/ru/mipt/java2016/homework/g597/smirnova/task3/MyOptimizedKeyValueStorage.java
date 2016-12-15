package ru.mipt.java2016.homework.g597.smirnova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class MyOptimizedKeyValueStorage<K, V>  implements KeyValueStorage<K, V> {
    private final Map<K, Long> dataOffset = new HashMap<>();
    private final SerializationStrategy<K> keySerializationStrategy;
    private final SerializationStrategy<V> valueSerializationStrategy;
    private final SerializationStrategy<Long> offsetSerializationStrategy = new LongSerializationStrategy();
    private Boolean isOpen = false;
    private String path;
    private File storage;
    private RandomAccessFile database;
    private ReadWriteLock lock;
    private int numberOfDeletions = 0;

    public MyOptimizedKeyValueStorage(String path, SerializationStrategy<K> newKeySerializationStrategy,
                             SerializationStrategy<V> newValueSerializationStrategy) throws IOException {
        this.path = path;
        if (!(new File(path)).exists()) {
            throw new FileNotFoundException("No such directory");
        }
        storage = new File(path + File.separator + "storage.db");
        database = new RandomAccessFile(path + File.separator + "database.db", "rw");
        isOpen = true;
        keySerializationStrategy = newKeySerializationStrategy;
        valueSerializationStrategy = newValueSerializationStrategy;
        lock = new ReentrantReadWriteLock();
        try {
            if (!storage.createNewFile()) {
                getData();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Can't create file");
        }
    }

    private void getData() {
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(storage)))) {
            int size = input.readInt();
            for (int i = 0; i < size; i++) {
                K key = keySerializationStrategy.readFromStream(input);
                Long offset = offsetSerializationStrategy.readFromStream(input);
                dataOffset.put(key, offset);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Can't read from file");
        }

    }

    private void checkForClosed() {
        if (!isOpen) {
            throw new IllegalStateException("File is closed");
        }
    }

    private void tryToUpdate() {
        if ((numberOfDeletions * 1.0) / dataOffset.size() > 0.5) {
            updateDatabase();
        }
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();
        try {
            checkForClosed();
            if (dataOffset.containsKey(key)) {
                try {
                    database.seek(dataOffset.get(key));
                    return valueSerializationStrategy.readFromStream(database);
                } catch (IOException e) {
                    throw new IllegalStateException("Can't read from database");
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            checkForClosed();
            return dataOffset.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();
        try {
            checkForClosed();
            if (exists(key)) {
                numberOfDeletions++;
            }
            dataOffset.put(key, database.length());
            database.seek(database.length());
            valueSerializationStrategy.writeToStream(database, value);
            tryToUpdate();
        } catch (IOException e) {
            throw new IllegalStateException("Can't write to database");
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            checkForClosed();
            dataOffset.remove(key);
            numberOfDeletions++;
            tryToUpdate();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();
        try {
            checkForClosed();
            return dataOffset.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkForClosed();
            return dataOffset.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();
        try {
            if (!isOpen) {
                return;
            }
            updateDatabase();
            database.close();
            isOpen = false;
            try (DataOutputStream offsetOutput = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(storage)))) {
                offsetOutput.writeInt(dataOffset.size());
                for (Map.Entry<K, Long> entry : dataOffset.entrySet()) {
                    keySerializationStrategy.writeToStream(offsetOutput, entry.getKey());
                    offsetSerializationStrategy.writeToStream(offsetOutput, entry.getValue());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updateDatabase() {
        numberOfDeletions = 0;
        try (RandomAccessFile tempDatabaseRandomAccess =                 
                     new RandomAccessFile(path + File.separator + "temp.db", "rw")) {
            for (Map.Entry<K, Long> entry : dataOffset.entrySet()) {
                K key = entry.getKey();
                Long offset = entry.getValue();
                database.seek(offset);
                V value = valueSerializationStrategy.readFromStream(database);
                dataOffset.put(key, tempDatabaseRandomAccess.length());
                tempDatabaseRandomAccess.seek(tempDatabaseRandomAccess.length());
                valueSerializationStrategy.writeToStream(tempDatabaseRandomAccess, value);
            }
            tempDatabaseRandomAccess.close();
            database.close();
            Path source = Paths.get(path + File.separator + "temp.db");
            Files.move(source, source.resolveSibling("database.db"), REPLACE_EXISTING);
            database = new RandomAccessFile(path + File.separator + "database.db", "rw");
        } catch (IOException e) {
            throw new IllegalStateException("Error in writing in file");
        }
    }
}
