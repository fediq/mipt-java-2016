package ru.mipt.java2016.homework.g596.ivanova.task3;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g596.ivanova.task2.Serialisation;

/**
 * @author julia
 * @since 19.11.16.
 */


/**
 * @param <K> - type of key.
 * @param <V> - type of value.
 */
public class BigDataStorage<K, V> implements KeyValueStorage<K, V> {
    /**
     * We will update our file when quantity of deleted elements reach this point.
     * It will clean file from waste entries, which appeared when some entries were deleted
     * from the map with offsets. We don't delete them physically in order to save time.
     */
    private final int maxDeleteCount = 2000000;

    /**
     * Max weight of elements we add but didn't write to file.
     */
    private final int maxAddWeight = 1024 * 1024; // 1 MB

    /**
     * Max weight of elements we cache.
     */
    private final int maxCachedWeight = 512 * 1024; // 0.5 MB

    private boolean isInitialized;

    /**
     * Serialisation instance for serialising keys.
     */
    private Serialisation<K> keySerialisation;

    /**
     * Serialisation instance for serialising values.
     */
    private Serialisation<V> valueSerialisation;

    /**
     * Name of our file.
     */
    private String filePath;

    /**
     * We'll use map from standard library to work with elements.
     * This map will be initialised at the beginning of work, when file is opened.
     * After the process of working with storage, elements from map will be written to the file.
     */
    private Map<K, V> map = new HashMap<>();

    /**
     * File where all the data stored.
     */
    private RandomAccessFile file;

    /**
     * How many elements were deleted since last sync.
     */
    private int deleteCount;

    /**
     * Weight of elements which were added since last sync.
     */
    private int addWeight;

    /**
     * Name of our twin file.
     */
    private String twinFilePath;

    /**
     * Collection that stores keys and relevant values of offset in file.
     */
    private Map<K, Long> offsets;

    /**
     * Cache for last accessed elements;
     */
    private Cache<K, V> cache;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    protected final void initStorage() throws IOException {
        file.seek(0); // go to the start
        map.clear();
        offsets.clear();

        K key;
        while (file.getFilePointer() < file.length()) {
            key = keySerialisation.read(file);

            if (map.containsKey(key)) {
                throw new RuntimeException("File contains two equal keys.");
            }

            long offset = file.getFilePointer();

            try {
                valueSerialisation.read(file);
            } catch (EOFException e) {
                throw new RuntimeException("No value for some key.");
            }

            offsets.put(key, offset);
        }

        isInitialized = true;
    }

    /**
     * @param path           - path to the directory with storage in filesystem.
     * @param name           - name of file with key-value storage.
     * @param kSerialisation - Serialisation appropriate for key type.
     * @param vSerialisation - Serialisation appropriate for value type.
     * @throws IOException - if I/O problem occurs.
     */
    public BigDataStorage(final String path, final String name,
            final Serialisation<K> kSerialisation, final Serialisation<V> vSerialisation)
            throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory.");
        }

        filePath = path + File.separator + name;
        keySerialisation = kSerialisation;
        valueSerialisation = vSerialisation;

        file = new RandomAccessFile(filePath, "rw");

        String twinName = name + "_twin";
        twinFilePath = path + File.separator + twinName;

        Weigher<K, V> weigher = (key, value) -> (int) ObjectSize.deepSizeOf(key) + (int) ObjectSize
                .deepSizeOf(value);
        cache = CacheBuilder.newBuilder()
                .maximumWeight(maxCachedWeight)
                .weigher(weigher)
                .build();

        offsets = new HashMap<>();
        initStorage();
    }

    public final V read(final K key) {
        writeLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            V value = cache.getIfPresent(key);
            if (value == null) {
                value = map.get(key);
                if (value == null) {
                    if (!offsets.containsKey(key)) {
                        return null;
                    }
                    long offset;
                    offset = offsets.get(key);
                    try {
                        file.seek(offset);
                        value = valueSerialisation.read(file);
                    } catch (IOException e) {
                        throw new MalformedDataException(e);
                    }
                }
                if (value != null) {
                    cache.put(key, value);
                }
            }
            return value;
        } finally {
            writeLock.unlock();
        }
    }

    public final boolean exists(final K key) {
        readLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            return map.containsKey(key) || offsets.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * @throws IOException
     */
    private void writeToFile() throws IOException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        file.seek(file.length());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keySerialisation.write(file, entry.getKey());
            offsets.put(entry.getKey(), file.getFilePointer());
            valueSerialisation.write(file, entry.getValue());
        }

        map.clear();
        addWeight = 0;
    }

    public final void write(final K key, final V value) {
        writeLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            addWeight += ObjectSize.deepSizeOf(key);
            addWeight += ObjectSize.deepSizeOf(value);

            if (addWeight > maxAddWeight) {
                try {
                    writeToFile();
                } catch (IOException e) {
                    throw new MalformedDataException(e);
                }
                addWeight = (int) (ObjectSize.deepSizeOf(key) + ObjectSize.deepSizeOf(value));
            }
            if (exists(key)) {
                ++deleteCount;
            }
            if (deleteCount > maxDeleteCount) {
                cleanFile();
            }
            map.put(key, value);
            offsets.put(key, -1L);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Cleans file up when maxDeleteCount is reached.
     */
    private void cleanFile() {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }

        try (RandomAccessFile twinFile = new RandomAccessFile(twinFilePath, "rw");
                DataOutputStream dataOutputStream = new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(
                                        twinFile.getFD())))) {
            twinFile.setLength(0);

            long newOffset = 0;
            for (Map.Entry<K, Long> entry : offsets.entrySet()) {
                if (entry.getValue() == -1) {
                    continue;
                }

                long offset = entry.getValue();
                try {
                    file.seek(offset);
                    newOffset += keySerialisation.write(dataOutputStream, entry.getKey());
                    offsets.put(entry.getKey(), newOffset);
                    valueSerialisation.write(dataOutputStream, valueSerialisation.read(file));
                } catch (IOException e) {
                    throw new MalformedDataException(e);
                }
            }
        } catch (IOException e) {
            throw new MalformedDataException(e);
        }
        try {
            file.close();
            Files.move(Paths.get(twinFilePath), Paths.get(filePath), REPLACE_EXISTING);
            file = new RandomAccessFile(filePath, "rw");
            deleteCount = 0;
        } catch (IOException e) {
            throw new MalformedDataException(e);
        }
    }

    public final void delete(final K key) {
        writeLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            map.remove(key);
            cache.invalidate(key);
            offsets.remove(key);
            ++deleteCount;
            if (deleteCount > maxDeleteCount) {
                cleanFile();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public final Iterator<K> readKeys() {
        readLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            try {
                writeToFile();
            } catch (IOException e) {
                throw new MalformedDataException(e);
            }
            return offsets.keySet().iterator();
        } finally {
            readLock.unlock();
        }
    }

    public final int size() {
        readLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            return offsets.size();
        } finally {
            readLock.unlock();
        }
    }

    public final void close() throws IOException {
        writeLock.lock();
        try {
            if (!isInitialized) {
                throw new MalformedDataException("Storage is closed.");
            }
            cache.cleanUp();

            if (deleteCount > 0) {
                cleanFile();
            }
            if (addWeight > 0) {
                writeToFile();
            }

            file.close();
            map.clear();
            offsets.clear();
            isInitialized = false;
        } finally {
            writeLock.unlock();
        }
    }
}
