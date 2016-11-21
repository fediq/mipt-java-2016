package ru.mipt.java2016.homework.g596.ivanova.task3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    private final int maxDeleteCount = 10000;

    /**
     * Max weight of elements we add but didn't write to file.
     */
    private final int maxAddWeight = 2 * 1024 * 1024; // 1 MB

    /**
     * Max weight of elements we cache.
     */
    private final int maxCachedWeight = 512 * 1024; // 1 MB

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
    private Map<K, V> map;

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
     * Twin for the main file. We will use them alternately during working with database.
     * Switch from one file to another will happen while cleaning file from waste data.
     */
    private RandomAccessFile twinFile;

    /**
     * Indicates if we are using main file - then true, or fileTwin - then false.
     */
    private boolean mainFileInUse;

    /**
     * Name of our twin file.
     */
    private String twinFilePath;

    /**
     * Collection that stores keys and relevant values of offset in file.
     */
    private Map<K, Long> offsets;

    /**
     * Entries we add to map, but didn't write to file are stored in map.
     */
    //private Map<K, V> lastAdded;

    /**
     * Cache for last accessed elements;
     */
    private Cache<K, V> cache;

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
                V value = valueSerialisation.read(file);
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

        map = new HashMap<>();
        filePath = path + File.separator + name;
        keySerialisation = kSerialisation;
        valueSerialisation = vSerialisation;

        file = new RandomAccessFile(filePath, "rw");

        String twinName = name + "_twin";
        twinFilePath = path + File.separator + twinName;
        twinFile = new RandomAccessFile(twinFilePath, "rw");

        deleteCount = 0;
        addWeight = 0;
        mainFileInUse = true;

        Weigher<K, V> weigher = (key, value) -> (int) ObjectSize.deepSizeOf(key) + (int) ObjectSize
                .deepSizeOf(value);
        cache = CacheBuilder.newBuilder().maximumWeight(maxCachedWeight).weigher(weigher).build();

        offsets = new HashMap<>();
        initStorage();
    }

    public final V read(final K key) throws MalformedDataException {
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
                long offset = offsets.get(key);
                RandomAccessFile usedFile;
                if (mainFileInUse) {
                    usedFile = file;
                } else {
                    usedFile = twinFile;
                }

                try {
                    usedFile.seek(offset);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    value = valueSerialisation.read(usedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (value != null) {
            cache.put(key, value);
        }
        return value;
    }

    public final boolean exists(final K key) throws MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        return cache.getIfPresent(key) != null || map.containsKey(key) || offsets.containsKey(key);
    }

    /**
     * @throws IOException
     */
    private void writeToFile() throws IOException, MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        RandomAccessFile usedFile;
        if (mainFileInUse) {
            usedFile = file;
        } else {
            usedFile = twinFile;
        }
        usedFile.seek(usedFile.length());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keySerialisation.write(usedFile, entry.getKey());
            offsets.put(entry.getKey(), usedFile.getFilePointer());
            valueSerialisation.write(usedFile, entry.getValue());
        }

        map.clear();
        addWeight = 0;
    }

    public final void write(final K key, final V value) {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        addWeight += ObjectSize.deepSizeOf(key);
        addWeight += ObjectSize.deepSizeOf(value);

        if (addWeight > maxAddWeight) {
            try {
                writeToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            addWeight = (int) (ObjectSize.deepSizeOf(key) + ObjectSize.deepSizeOf(value));
        }
        map.put(key, value);
        offsets.put(key, -1L);
    }

    /**
     * Cleans file up when maxDeleteCount is reached.
     */
    private void fileCleaner() throws IOException, MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        RandomAccessFile newFile;
        RandomAccessFile oldFile;
        if (mainFileInUse) {
            oldFile = file;
            newFile = twinFile;
        } else {
            oldFile = twinFile;
            newFile = file;
        }
        mainFileInUse = !mainFileInUse;

        newFile.setLength(0);
        newFile.seek(0);
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            if (entry.getValue() == -1) {
                continue;
            }
            keySerialisation.write(newFile, entry.getKey());
            long offset = entry.getValue();
            oldFile.seek(offset);
            offsets.put(entry.getKey(), newFile.getFilePointer());
            valueSerialisation.write(newFile, valueSerialisation.read(oldFile));
        }

        oldFile.setLength(0);
        deleteCount = 0;
    }

    public final void delete(final K key) throws MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        map.remove(key);
        cache.invalidate(key);
        offsets.remove(key);
        ++deleteCount;
        if (deleteCount == maxDeleteCount) {
            try {
                fileCleaner();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final Iterator<K> readKeys() throws MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return offsets.keySet().iterator();
    }

    public final int size() throws MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        return offsets.size();
    }

    public final void close() throws IOException, MalformedDataException {
        if (!isInitialized) {
            throw new MalformedDataException("Storage is closed.");
        }
        cache.cleanUp();

        if (deleteCount > 0) {
            fileCleaner();
        }
        if (addWeight > 0) {
            writeToFile();
        }

        twinFile.close();

        File twin = new File(twinFilePath, "");
        if (mainFileInUse) {
            twin.delete();
            file.close();
        } else {
            file.close();
            File mainFile = new File(filePath);
            twin.renameTo(mainFile);
        }

        mainFileInUse = true;
        map.clear();
        offsets.clear();
        isInitialized = false;
    }
}
