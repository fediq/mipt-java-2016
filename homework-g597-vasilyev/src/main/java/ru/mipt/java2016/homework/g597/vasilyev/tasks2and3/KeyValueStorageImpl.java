package ru.mipt.java2016.homework.g597.vasilyev.tasks2and3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by mizabrik on 30.10.16.
 */
public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private Map<K, Long> offsets;
    private RandomAccessFile db;
    private RandomAccessFile valuesFiles;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private boolean open;
    private ReadWriteLock lock;

    public KeyValueStorageImpl(String path,
                               Serializer<K> keySerializer,
                               Serializer<V> valueSerializer) throws IOException, ConcurrentStorageAccessException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        lock = new ReentrantReadWriteLock();

        Path dbPath = FileSystems.getDefault().getPath(path, "db");
        Path valuesPath = FileSystems.getDefault().getPath(path, "values");
        try {
            db = new RandomAccessFile(dbPath.toString(), "rw");
            valuesFiles = new RandomAccessFile(valuesPath.toString(), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("RandomAccessFile(path, \"rw\") throwed FileNotFoundException");
        }

        try {
            db.getChannel().tryLock();
        } catch (OverlappingFileLockException e) {
            throw new ConcurrentStorageAccessException();
        }

        offsets = new HashMap<>();

        if (db.length() > 0) {
            while (db.getFilePointer() < db.length()) {
                K key = keySerializer.read(db);
                long offset = db.readLong();
                offsets.put(key, offset);
            }
        }

        open = true;
    }

    @Override
    public V read(K key) {
        lock.readLock().lock();

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
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        lock.readLock().lock();

        try {
            return offsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        lock.writeLock().lock();

        checkOpen();
        try {
            valuesFiles.seek(valuesFiles.length());
            offsets.put(key, valuesFiles.getFilePointer());
            valueSerializer.write(value, valuesFiles);
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
            offsets.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        lock.readLock().lock();

        try {
            return offsets.keySet().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();

        try {
            return offsets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();
        open = false;

        try {
            db.seek(0);
            db.setLength(0);
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializer.write(entry.getKey(), db);
                db.writeLong(entry.getValue());
            }
            db.close();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void checkOpen() {
        if (!open) {
            throw new RuntimeException("Writing to closed KeyValueStorage");
        }
    }

    private void trim() {
        ;
    }

    private V readValue(long offset) throws IOException {
        valuesFiles.seek(offset);
        return valueSerializer.read(valuesFiles);
    }
}
