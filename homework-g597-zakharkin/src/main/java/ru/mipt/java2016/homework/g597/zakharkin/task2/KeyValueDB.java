package ru.mipt.java2016.homework.g597.zakharkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of NoSQL DB - Key-Value Storage (not all types)
 *
 * @author Ilya Zakharkin
 * @since 24.10.16.
 */
public class KeyValueDB<K, V> implements KeyValueStorage<K, V> {
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private RandomAccessFile dbFile;
    private HashMap<K, V> keyValueMap;
    private FileLock lock;

    public KeyValueDB(String path, String name,
                      Serializer<K> keySerializerArg,
                      Serializer<V> valueSerializerArg) throws IOException, OverlappingFileLockException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Incorrect directory");
        }
        String dbPath = path + File.separator + name;
        File file = new File(dbPath);
        dbFile = new RandomAccessFile(file, "rw");
        // Multithreaing check (already opened check)
        FileChannel dbFileChannel = dbFile.getChannel();
        lock = dbFileChannel.tryLock();
        // Moving on
        keyValueMap = new HashMap<>();
        keySerializer = keySerializerArg;
        valueSerializer = valueSerializerArg;
        if (!file.createNewFile()) {
            loadExistingKVDB();
        }
    }

    void loadExistingKVDB() throws IOException {
        dbFile.seek(0);
        keyValueMap.clear();
        while (dbFile.getFilePointer() < dbFile.length()) {
            K key = keySerializer.read(dbFile);
            V value = valueSerializer.read(dbFile);
            if (keyValueMap.containsKey(key)) {
                throw new IOException("Repeated keys");
            } else {
                keyValueMap.put(key, value);
            }
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        return keyValueMap.get(key);
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        return keyValueMap.containsKey(key);
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        keyValueMap.put(key, value);
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {
        keyValueMap.remove(key);
    }

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() throws ConcurrentModificationException {
        return keyValueMap.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return keyValueMap.size();
    }

    @Override
    public void close() throws IOException, OverlappingFileLockException {
        dbFile.setLength(0);
        dbFile.seek(0);
        for (Map.Entry<K, V> item : keyValueMap.entrySet()) {
            keySerializer.write(dbFile, item.getKey());
            valueSerializer.write(dbFile, item.getValue());
        }
        keyValueMap.clear();
        dbFile.close();
    }
}