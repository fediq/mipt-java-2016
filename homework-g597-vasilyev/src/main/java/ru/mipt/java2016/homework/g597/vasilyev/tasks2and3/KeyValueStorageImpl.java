package ru.mipt.java2016.homework.g597.vasilyev.tasks2and3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by mizabrik on 30.10.16.
 */
public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private final Map<K, Long> offsets = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private boolean open;
    private RandomAccessFile db;
    private RandomAccessFile valuesFile;
    private long valuesCount;
    private boolean readyToWrite = false;

    public KeyValueStorageImpl(String path,
                               Serializer<K> keySerializer,
                               Serializer<V> valueSerializer) throws IOException, ConcurrentStorageAccessException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        Path dbPath = Paths.get(path, "db");
        db = openRandomAccessFile(dbPath);
        lockDB();
        if (db.length() > 0) {
            readDB();
        }

        Path valuesPath = Paths.get(path, "values");
        if (Files.exists(valuesPath) && offsets.size() > 2 * valuesCount) {
            Path oldValuesPath = Paths.get(path, "values.old");
            Files.move(valuesPath, oldValuesPath);
            trimStorage(oldValuesPath, valuesPath);
            Files.delete(oldValuesPath);
        }
        valuesFile = openRandomAccessFile(valuesPath);

        open = true;
    }

    @Override
    public V read(K key) {
        lock.writeLock().lock();

        try {
            checkOpen();

            Long offset = offsets.get(key);
            if (offset == null) {
                return null;
            }

            return readValue(offset);
        } catch (IOException e) {
            throw new RuntimeException("Этого не может быть, потому что не может быть никогда!", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();

        try {
            checkOpen();
            return offsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();

        try {
            checkOpen();

            if (!readyToWrite) {
                valuesFile.seek(valuesFile.length());
                readyToWrite = true;
            }

            offsets.put(key, valuesFile.getFilePointer());
            valueSerializer.write(value, valuesFile);
            valuesCount += 1;
        } catch (IOException e) {
            throw new RuntimeException("Этого не может быть, потому что не может быть никогда!", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();

        try {
            checkOpen();
            offsets.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();

        try {
            checkOpen();
            return offsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();

        try {
            checkOpen();
            return offsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();

        try {
            if (!open) {
                return;
            }

            open = false;
            db.seek(0);
            db.setLength(0);
            db.writeLong(valuesCount);
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializer.write(entry.getKey(), db);
                db.writeLong(entry.getValue());
            }
            db.close();
            valuesFile.close();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void checkOpen() {
        if (!open) {
            throw new RuntimeException("Writing to closed KeyValueStorage");
        }
    }

    private void trimStorage(Path oldPath, Path newPath) throws IOException {
        try (RandomAccessFile valuesFile = openRandomAccessFile(oldPath);
             DataOutputStream newValues = new DataOutputStream(
                     new BufferedOutputStream(new FileOutputStream(newPath.toString())))) {
            long newOffset = 0;
            valuesFile.getFilePointer();
            for (Map.Entry<K, Long> key : offsets.entrySet()) {
                V value = readValue(key.getValue());
                offsets.put(key.getKey(), newOffset);
                valueSerializer.write(value, newValues);
                newOffset = valueSerializer.size(value);
            }
        }
    }

    private V readValue(long offset) throws IOException {
        valuesFile.seek(offset);
        readyToWrite = false;
        return valueSerializer.read(valuesFile);
    }

    private RandomAccessFile openRandomAccessFile(Path path) {
        try {
            return new RandomAccessFile(path.toString(), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("RandomAccessFile(path, \"rw\") throwed FileNotFoundException");
        }
    }

    private void lockDB() throws ConcurrentStorageAccessException, IOException {
        try {
            if (db.getChannel().tryLock() == null) {
                throw new ConcurrentStorageAccessException();
            }
        } catch (OverlappingFileLockException e) {
            throw new ConcurrentStorageAccessException();
        }
    }

    private void readDB() throws IOException {
        valuesCount = db.readLong();
        while (db.getFilePointer() < db.length()) {
            K key = keySerializer.read(db);
            long offset = db.readLong();
            offsets.put(key, offset);
        }
    }
}
