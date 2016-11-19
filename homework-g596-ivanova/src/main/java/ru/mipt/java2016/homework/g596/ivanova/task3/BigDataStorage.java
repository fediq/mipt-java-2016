package ru.mipt.java2016.homework.g596.ivanova.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import ru.mipt.java2016.homework.g596.ivanova.task2.BestKeyValueStorageEver;
import ru.mipt.java2016.homework.g596.ivanova.task2.Serialisation;
import java.lang.instrument.Instrumentation;

/**
 * @author julia
 * @since 19.11.16.
 */

/**
 * @param <K> - type of key.
 * @param <V> - type of value.
 */
public class BigDataStorage<K, V> extends BestKeyValueStorageEver<K, V> {
    /**
     * We will update our file when quantity of deleted elements reach this point.
     * It will clean file from waste entries, which appeared when some entries were deleted
     * from the map with offsets. We don't delete them physically in order to save time.
     */
    private final int maxDeleteCount = 10000;

    /**
     * Max weight of elements we add but didn't write to file.
     */
    private final int maxAddWeight = 12 * 1024 * 2024; // 12 MB

    /**
     * How many elements were deleted since last sync.
     */
    private int deleteCount;

    /**
     * How many elements were added since last sync.
     */
    private int addCount;

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
    private String twinFileName;

    /**
     * Collection that stores keys and relevant values of offset in file.
     */
    private Map<K, Integer> offsets;

    /**
     * Entries we add to map, but didn't write to file.
     */
    private Map<K, V> lastAdded;

    /**
     * Cache for last accesed elements;
     */
    private LoadingCache<K, V> cache;

    @Override
    protected final void initStorage() throws IOException {
        RandomAccessFile usedFile;
        if (mainFileInUse) {
            usedFile = file;
        } else {
            usedFile = twinFile;
        }

        usedFile.seek(0); // go to the start
        map.clear();
        offsets.clear();

        K key;
        V value;
        while (usedFile.getFilePointer() < usedFile.length()) {
            key = keySerialisation.read(usedFile);

            try {
                value = valueSerialisation.read(usedFile);
            } catch (EOFException e) {
                throw new RuntimeException("No value for some key.");
            }

            map.put(key, value);
        }
    }

    /**
     * @param path - path to the directory with storage in filesystem.
     * @param name - name of file with key-value storage.
     * @param twinName - name of twin file.
     * @param kSerialisation - Serialisation appropriate for key type.
     * @param vSerialisation - Serialisation appropriate for value type.
     * @throws IOException - if I/O problem occures.
     */
    public BigDataStorage(final String path,
            final String name,
            final String twinName,
            final Serialisation<K> kSerialisation,
            final Serialisation<V> vSerialisation) throws IOException {
        super(path, name, kSerialisation, vSerialisation);

        twinFileName = twinName;
        String twinStoragePath = path + File.separator + twinName;
        twinFile = new RandomAccessFile(twinStoragePath, "rw");

        deleteCount = 0;
        addCount = 0;
        mainFileInUse = true;

        Weigher<K, V> weigher = new Weigher<K, V>() {
            private Instrumentation instrumentation;
            @Override
            public int weigh(K key, V value) {
                return (int) instrumentation.getObjectSize(key) + (int) instrumentation.getObjectSize(value);
            }
        };
        cache = CacheBuilder.newBuilder()
                .maximumWeight(maxAddWeight)
                .weigher(weigher);
    }
}