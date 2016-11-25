package ru.mipt.java2016.homework.g597.zakharkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.zakharkin.task2.Serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ilya on 17.11.16.
 */
public class KeyValueStorageOptimized<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private static final int MAX_CACHE_SIZE = 10;
    private static final int MAX_BUF_SIZE = 1000;
    private static final String DB_NAME = "key_value_storage";
    private static final String OFFSET_NAME = "offsets";
    private String dbPath;
    private Boolean is_open = false;
    private Map<K, V> lruCache;
    private Map<K, Long> offsets = new HashMap<>();
    private HashMap<K, V> buffer;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private RandomAccessFile dbFile;
    private RandomAccessFile offsetsFile;
    private ReadWriteLock lock;

    public KeyValueStorageOptimized(String path,
                                    Serializer<K> keySerializerArg,
                                    Serializer<V> valueSerializerArg) throws OverlappingFileLockException {
        lruCache = new LinkedHashMap<K, V>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
        dbPath = path;
        lruCache = Collections.synchronizedMap(lruCache);
        buffer = new HashMap<>();
        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        lock = new ReentrantReadWriteLock();
        try {
            Boolean alreadyExists = initializeFiles(path);
            offsetsFile.getChannel().lock();
            dbFile.getChannel().lock();
            if (alreadyExists) {
                loadExistingKVDB();
            }
        } catch (IOException e) {
            System.out.println("Can`t create/open the DB file.");
        }
        is_open = true;
    }

    private Boolean initializeFiles(String path) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Incorrect directory");
        }
        String commonPath = dbPath + File.separator;
        File file = new File(commonPath + DB_NAME);
        dbFile = new RandomAccessFile(file, "rw");
        File file1 = new File(commonPath + OFFSET_NAME);
        offsetsFile = new RandomAccessFile(file1, "rw");
        if (file.exists() && file1.exists()) {
            return true;
        }
        return false;
    }

    private void loadExistingKVDB() throws IOException {
        buffer.clear();
        offsets.clear();
        lruCache.clear();
        offsetsFile.seek(0);
        while (offsetsFile.getFilePointer() < offsetsFile.length()) {
            K key = keySerializer.read(offsetsFile);
            Long offset = offsetsFile.readLong();
            offsets.put(key, offset);
        }
    }

    private void checkStorageIsOpen() {
        try {
            if (!is_open) {
                throw new RuntimeException("Storage is not opened!");
            }
        } catch (RuntimeException e) {
            System.out.println("Storage is closed");
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        checkStorageIsOpen();
        lock.readLock().lock();
        V value = lruCache.get(key);
        try {
            if (value != null) {
                return value;
            }
            Long offset = offsets.get(key);
            if (offset == null) {
                return null;
            }
            if (offset == -1) {
                value = buffer.get(key);
            } else {
                dbFile.seek(offset);
                value = valueSerializer.read(dbFile);
                lruCache.put(key, value);
            }
        } catch (IOException e) {
            System.out.println("Can`t read the data from the DB file.");
        } finally {
            lock.readLock().unlock();
        }
        return value;
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        lock.readLock().lock();
        try {
            return offsets.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        checkStorageIsOpen();
        lock.writeLock().lock();
        try {
            if (buffer.size() >= MAX_BUF_SIZE) {
                dumpToFile();
            }
            offsets.put(key, new Long(-1));
            buffer.put(key, value);
            lruCache.put(key, value);
        } catch (IOException e) {
            System.out.println("Can`t write the data to the DB file.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void dumpToFile() throws IOException {
        dbFile.seek(dbFile.length());
        for (Map.Entry<K, V> entry : buffer.entrySet()) {
            offsets.put(entry.getKey(), dbFile.getFilePointer());
            valueSerializer.write(dbFile, entry.getValue());
        }
        buffer.clear();
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {
        offsets.remove(key);
        buffer.remove(key);
        lruCache.remove(key);
    }

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() throws ConcurrentModificationException {
        checkStorageIsOpen();
        return offsets.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return offsets.size();
    }

    private void refreshStorageFile() throws IOException {
        File newStorageFile = new File(dbPath + File.separator + DB_NAME + "__tmp");
        RandomAccessFile newDBFile = new RandomAccessFile(newStorageFile, "rw");
        newDBFile.seek(0);
        offsetsFile.setLength(0);
        offsetsFile.seek(0);
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            Long offset = entry.getValue();
            dbFile.seek(offset);
            V value = valueSerializer.read(dbFile);
            keySerializer.write(offsetsFile, entry.getKey());
            offsetsFile.writeLong(newDBFile.getFilePointer());
            valueSerializer.write(newDBFile, value);
        }
        dbFile.close();
        File oldStorageFile = new File(dbPath + File.separator + DB_NAME);
        oldStorageFile.delete();
        newStorageFile.renameTo(new File(dbPath + File.separator + DB_NAME));
        newDBFile.close();
    }

    @Override
    public void close() throws IOException, OverlappingFileLockException {
        if (!is_open) {
            return;
        }
        lock.writeLock().lock();
        try {
            dumpToFile();
            offsetsFile.setLength(0);
            offsetsFile.seek(0);
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                keySerializer.write(offsetsFile, entry.getKey());
                offsetsFile.writeLong(entry.getValue());
            }
            refreshStorageFile();
        } finally {
            offsets.clear();
            offsetsFile.close();
            is_open = false;
            lock.writeLock().unlock();
        }
    }
}