package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * @author Vardan Manucharyan
 * @since 20.11.2016.
 */
public class OptimisedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final long MAX_CACHE_SIZE = 100L;
    private static final double FILLING_PERCENTAGE = 0.5; //when we need clean storage from deleted values
    private static long deletedCount; //number of deleted elements

    private final SerializationStrategyRandomAccess<K> keySerializationStrategy;
    private final SerializationStrategyRandomAccess<V> valueSerializationStrategy;

    //consist keys and offsets(the pair of begin and length)
    private Map<K, Long> base = new HashMap<>();
    private Map<K, V> cache = new HashMap<>();

    private long maxOffset;
    private final String pathname;
    private final String storageName = "storage.txt";
    private RandomAccessFile mapStorage;
    private RandomAccessFile storage;
    private final String mapStorageName = "mapStorage.txt";

    private File mutexFile; // для многопоточности
    private boolean isClosed;

    public OptimisedKeyValueStorage(SerializationStrategyRandomAccess<K> keySerializationStrategy,
                                    SerializationStrategyRandomAccess<V> valueSerializaionStrategy,
                                    String path) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializaionStrategy;
        maxOffset = 0L;
        deletedCount = 0L;
        pathname = path;
        isClosed = false;

        mutexFile = new File(pathname, "Mutex");
        if (!mutexFile.createNewFile()) {
            throw new RuntimeException("Can't synchronize!");
        }

        File directory = new File(pathname);
        if (!directory.isDirectory()) {
            throw new RuntimeException("wrong path");
        }

        try {
            File file = new File(path, mapStorageName);
            boolean exist = file.exists();

            mapStorage = new RandomAccessFile(file, "rw");
            if (exist) {
                downloadDataFromStorage();
            }
            storage = new RandomAccessFile(new File(pathname, storageName), "rw");

        } catch (IOException exception) {
            throw new RuntimeException("Can't create a storage!");
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public synchronized V read(K key) {
        isClose();

        if (!exists(key)) {
            return null;
        } else {
            try {
                if (cache.get(key) != null) {
                    return cache.get(key);
                }

                Long offset = base.get(key);
                storage.seek(offset);
                V res = valueSerializationStrategy.deserializeFromFile(storage);
                return res;
            } catch (Exception exception) {
                throw new RuntimeException("Can't read from storage");
            }
        }
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        isClose();

        if (cache.containsKey(key)) {
            return true;
        }
        return base.containsKey(key);
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public synchronized void write(K key, V value) {
        isClose();

        cache.put(key, value);
        base.put(key, 0L);

        if (cache.size() > MAX_CACHE_SIZE) {
            writeCacheToStorage();
        }
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public synchronized void delete(K key) {
        isClose();

        cache.remove(key);
        base.remove(key);
        deletedCount++;

        if (deletedCount / size() > FILLING_PERCENTAGE) {
            reorganiseStorage();
        }
    }

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() {
        isClose();

        return base.keySet().iterator();

    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        isClose();

        return base.size();
    }

    @Override
    public synchronized void close() {
        if (isClosed) {
            return;
        }

        reorganiseStorage();
        uploadDataToStorage();

        try {
            mapStorage.close();
            storage.close();
        } catch (IOException excetion) {
            throw new RuntimeException("Can't close storage");
        } finally {
            isClosed = true;
            cache.clear();
            base.clear();
            mutexFile.delete();
        }
    }

    private void downloadDataFromStorage() {
        try {

            int count = mapStorage.readInt();

            for (int i = 0; i < count; i++) {
                K key = keySerializationStrategy.deserializeFromFile(mapStorage);
                Long tmp = mapStorage.readLong();
                base.put(key, tmp);
                maxOffset = Math.max(maxOffset, tmp);
            }
        } catch (IOException exception) {
            base.clear();
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    private void uploadDataToStorage() {
        try {
            mapStorage.close();
            File file = new File(pathname, mapStorageName);
            assert (file.delete());
            mapStorage = new RandomAccessFile(new File(pathname, mapStorageName), "rw");

            mapStorage.writeInt(size());
            for (HashMap.Entry<K, Long> entry : base.entrySet()) {
                keySerializationStrategy.serializeToFile(entry.getKey(), mapStorage);
                mapStorage.writeLong(entry.getValue());
            }
        } catch (IOException exception) {
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    private void writeCacheToStorage() {
        isClose();

        if (cache.isEmpty()) {
            return;
        }

        try {
            storage.seek(maxOffset);
            for (HashMap.Entry<K, V> entry : cache.entrySet()) {
                valueSerializationStrategy.serializeToFile(entry.getValue(), storage);
                base.put(entry.getKey(), maxOffset);
                maxOffset = storage.getFilePointer();
            }

            cache.clear();

        } catch (IOException exception) {
            throw new RuntimeException("Can't write cache on the disk");
        }

    }

    private void reorganiseStorage() {

        File file1 = new File(pathname, storageName);
        File file = new File(pathname, "newStorage.txt");

        try (DataOutputStream newStorage = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {

            writeCacheToStorage();

            for (HashMap.Entry<K, Long> entry : base.entrySet()) {
                storage.seek(entry.getValue());
                Long tmp = (long) newStorage.size();
                valueSerializationStrategy.serializeToFile(read(entry.getKey()), newStorage);
                base.put(entry.getKey(), tmp);
            }
            deletedCount = 0;

            storage.close();
            assert (file1.delete());

            newStorage.close();
            assert (file.renameTo(file1));
            storage = new RandomAccessFile(new File(pathname, storageName), "rw");

        } catch (IOException exception) {
            throw new RuntimeException("Can't reorganise storage!");
        }
    }

    private void isClose() {
        if (isClosed) {
            throw new IllegalStateException("Can't write: storage is closed");
        }
    }

}

