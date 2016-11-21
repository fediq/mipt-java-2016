package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.manucharyan.task3.SerializationStrategyRandomAccess;

import static java.lang.Math.max;

/**
 * @author Vardan Manucharyan
 * @since 20.11.2016.
 */
public class OptimisedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final long MAX_CACHE_SIZE = 100L;

    private SerializationStrategyRandomAccess<K> keySerializationStrategy;
    private SerializationStrategyRandomAccess<V> valueSerializationStrategy;

    //consist keys and offsets(the pair of begin and length)
    private HashMap<K, Pair<Long, Long>> base = new HashMap<>();
    private HashMap<K, V> cache = new HashMap<>();

    private long maxOffset;
    private RandomAccessFile storage;
    private RandomAccessFile mapStorage;

    private File mutexFile; // для многопоточности
    private boolean isClosed;

    public OptimisedKeyValueStorage(SerializationStrategyRandomAccess<K> keySerializationStrategy,
                               SerializationStrategyRandomAccess<V> valueSerializaionStrategy,
                               String path) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializaionStrategy;
        maxOffset = 0L;

        mutexFile = new File(path, "Mutex");
        if (!mutexFile.createNewFile()) {
            throw new RuntimeException("Can't synchronize!");
        }

        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new RuntimeException("wrong path");
        }

        File file = new File(path, "storage.db");
        storage = new RandomAccessFile(file, "rw");

        File file2 = new File(path, "mapStorage.db");
        mapStorage = new RandomAccessFile(file2, "rw");

        if (file.exists() && file.exists()) {
            uploadDataFromStorage();
        } else if (!file.createNewFile() || file2.createNewFile()) {
            throw new RuntimeException("Can't create a storage!");
        }

        isClosed = false;
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        if (!exists(key)) {
            return null;
        } else {
            try {
                if (cache.get(key) != null) {
                    return cache.get(key);
                }

                Pair<Long, Long> offset = base.get(key);
                storage.seek(offset.getKey());
                return valueSerializationStrategy.deserializeFromFile(storage);
            } catch(Exception exception) {
                throw new RuntimeException("Can't read from storage");
            }
        }
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        if(isClosed) return false;
        if(cache.containsKey(key)) return true;
        return base.containsKey(key);
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        if (isClosed) {
            throw new RuntimeException("Can't write: storage is closed");
        } else {
            cache.put(key, value);
            base.put(key, new Pair<>(0L, 0L));

            if(cache.size() > MAX_CACHE_SIZE) {
                writeCacheToStorage();
            }
        }
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {
        if (isClosed) {
            throw new RuntimeException("Can't delete: storage is closed");
        } else {
            cache.remove(key);
            base.remove(key);
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
        if (isClosed) {
            throw new RuntimeException("Can't iterate: storage is closed");
        } else {
            return base.keySet().iterator();
        }
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return base.size();
    }

    @Override
    public void close() {
        reorganiseStorage();
        downnloadDataToStorage();
        isClosed = true;
        cache.clear();
        base.clear();
        mutexFile.delete();
    }

    private void uploadDataFromStorage() {
        try {

            int count;
            count = mapStorage.readInt();

            for (int i = 0; i < count; i++) {
                K key = keySerializationStrategy.deserializeFromFile(mapStorage);
                Long tmp1 = mapStorage.readLong();
                Long tmp2 = mapStorage.readLong();
                base.put(key, new Pair<>(tmp1, tmp2));
                maxOffset = max(maxOffset, tmp1 + tmp2);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    private void downnloadDataToStorage() {
        try {

            writeCacheToStorage();

            mapStorage.writeInt(size());

            for (HashMap.Entry<K, Pair<Long, Long>> entry : base.entrySet()) {
                keySerializationStrategy.serializeToFile(entry.getKey(), mapStorage);
                mapStorage.writeLong(entry.getValue().getKey());
                mapStorage.writeLong(entry.getValue().getValue());
            }
        } catch (IOException exception) {
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    private void writeCacheToStorage(){
        if (isClosed) {
            throw new RuntimeException("Can't write: storage is closed");
        } else {
            try {
                for (HashMap.Entry<K, V> entry : cache.entrySet()) {
                    storage.seek(maxOffset);
                    valueSerializationStrategy.serializeToFile(entry.getValue(), storage);
                    long curOffset = storage.getFilePointer();
                    base.put(entry.getKey(), new Pair<>(maxOffset, curOffset - maxOffset));
                    maxOffset = curOffset;
                }
                cache.clear();

            } catch (IOException exception) {
                throw new RuntimeException("Can't write cache on the disk");
            }
        }
    }

    private void reorganiseStorage() {

    }
}

