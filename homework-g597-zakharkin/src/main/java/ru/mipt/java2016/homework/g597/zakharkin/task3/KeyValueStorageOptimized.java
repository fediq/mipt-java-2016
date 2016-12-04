package ru.mipt.java2016.homework.g597.zakharkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.zakharkin.task2.Serializer;

import java.io.*;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ilya on 17.11.16.
 */
public class KeyValueStorageOptimized<K, V> implements KeyValueStorage<K, V>, AutoCloseable {
    private static final int MAX_CACHE_SIZE = 10;
    private static final int MAX_BUF_SIZE = 100;
    private static final String DB_NAME = "storage";
    private static final String OFFSET_NAME = "offsets";
    private final String dbPath;
    private Boolean isOpen = false;
    private Map<K, V> lruCache;
    private Map<K, Long> offsets = new HashMap<>();
    private Map<K, V> buffer = new HashMap<>();
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private RandomAccessFile dbFile;
    private RandomAccessFile offsetsFile;
    private ReadWriteLock lock;
    private int trashCount = 0;

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
        isOpen = true;
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
        if (!isOpen) {
            throw new IllegalStateException("Storage is not opened!");
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
            offsets.put(key, (long) -1);
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
        checkStorageIsOpen();
        offsets.remove(key);
        buffer.remove(key);
        lruCache.remove(key);
        trashCount += 1;
        if (trashCount >= offsets.size()) {
            try {
                refreshStorageFile();
            } catch (IOException e) {
                System.out.println("Can`t refresh the db file.");
            }
            trashCount = 0;
        }
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
        lock.writeLock().lock();
        lock.readLock().lock();
        File newStorageFile = new File(dbPath + File.separator + DB_NAME + "__tmp");
        newStorageFile.createNewFile();
        try (DataOutputStream outputStreamStorage =
                     new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newStorageFile)))) {
            offsetsFile.setLength(0);
            offsetsFile.seek(0);
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                Long offset = entry.getValue();
                dbFile.seek(offset);
                V value = valueSerializer.read(dbFile);
                keySerializer.write(offsetsFile, entry.getKey());
                offsetsFile.writeLong(newStorageFile.length());
                valueSerializer.write(outputStreamStorage, value);
            }
            outputStreamStorage.close();
            dbFile.close();
            Path tempName = Paths.get(dbPath, DB_NAME + "__tmp");
            Path realName = Paths.get(dbPath, DB_NAME);
            Files.move(tempName, realName, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            lock.readLock().unlock();
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException, OverlappingFileLockException {
        if (!isOpen) {
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
            if (trashCount >= offsets.size()) {
                refreshStorageFile();
            }
        } finally {
            offsets.clear();
            offsetsFile.close();
            dbFile.close();
            isOpen = false;
            lock.writeLock().unlock();
        }
    }
}