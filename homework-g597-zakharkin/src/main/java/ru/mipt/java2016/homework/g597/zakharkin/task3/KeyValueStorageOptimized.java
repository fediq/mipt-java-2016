package ru.mipt.java2016.homework.g597.zakharkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.zakharkin.task2.Serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ilya on 17.11.16.
 */
public class KeyValueStorageOptimized<K, V> implements KeyValueStorage<K, V> {
    private final int maxCacheSize = 10;
    private final int maxBufSize = 1000;
    private final String dbName = "key_value_storage";
    private final String offsetName = "offsets";
    private Map<K, V> lruCache;
    private HashMap<K, Long> offsets;
    private HashMap<K, V> buffer;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private RandomAccessFile dbFile;
    private RandomAccessFile offsetsFile;
    private FileLock lock;

    public KeyValueStorageOptimized(String path,
                                    Serializer<K> keySerializerArg,
                                    Serializer<V> valueSerializerArg) throws OverlappingFileLockException {
        lruCache = new LinkedHashMap<K, V>(maxCacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCacheSize;
            }
        };
        lruCache = (Map) Collections.synchronizedMap(lruCache);
        offsets = new HashMap<>();
        buffer = new HashMap<>();
        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        try {
            Boolean alreadyExists = initializeFiles(path);
            if (alreadyExists) {
                loadExistingKVDB();
            }
            FileChannel dbFileChannel = dbFile.getChannel();
            lock = dbFileChannel.tryLock();
        } catch (IOException e) {
            System.out.println("Can`t create/open the DB file.");
        }
    }

    private Boolean initializeFiles(String path) throws FileNotFoundException, IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Incorrect directory");
        }
        String commonPath = path + File.separator;
        File file = new File(commonPath + dbName);
        dbFile = new RandomAccessFile(file, "rw");
        File file1 = new File(commonPath + offsetName);
        offsetsFile = new RandomAccessFile(file1, "rw");
        if (file.exists() && file1.exists()) {
            return true;
        }
        return false;
    }

    void loadExistingKVDB() throws IOException {
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

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        V value = lruCache.get(key);
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
            try {
                dbFile.seek(offset);
                value = valueSerializer.read(dbFile);
                lruCache.put(key, value);
            } catch (IOException e) {
                System.out.println("Can`t read the data from the DB file.");
            }
        }
        return value;
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        return offsets.containsKey(key);
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        if (buffer.size() >= maxBufSize) {
            try {
                dumpToFile();
            } catch (IOException e) {
                System.out.println("Can`t write the data to the DB file.");
            }
        }
        offsets.put(key, new Long(-1));
        buffer.put(key, value);
        lruCache.put(key, value);
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
        return offsets.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return offsets.size();
    }

    @Override
    public void close() throws IOException, OverlappingFileLockException {
        dumpToFile();
        dbFile.close();
        offsetsFile.setLength(0);
        offsetsFile.seek(0);
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            keySerializer.write(offsetsFile, entry.getKey());
            offsetsFile.writeLong(entry.getValue());
        }
        offsets.clear();
        offsetsFile.close();
    }
}
