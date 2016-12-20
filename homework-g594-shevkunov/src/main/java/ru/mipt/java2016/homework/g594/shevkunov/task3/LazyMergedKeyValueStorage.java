package ru.mipt.java2016.homework.g594.shevkunov.task3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


/**
 * Implementation of KeyValueStorage based on merging files
 * This strategy proceed write and read operations for real O(1) cost
 * and delete and update (= delete + write) operation for amortizated O(1) cost
 *
 * We have two files contains base: head-file and base-file
 * Head file contains metadata of base and pair Key-Offset
 * it helps to find Value by offset.
 *
 * Base-file contains values and unused values.
 *
 * Write operation: we add key to header (LazyMergedKeyValueStorageHeader) and
 * proceed value writing to base-file. (real O(1))
 *
 * Read operation: we key file-offset from header and read data from base-file. (real O(1))
 *
 * Delete operation: we calculating "lazy" number. When we delete file we only remove key from
 * header and increase lazy number. When lazy number becomes greater than quantity of real-existing
 * elements, we rebuild the base-file: create new base-file and copy only needed values (which key's
 * are contained by header). After that we set the lazy number to zero.
 * (Because of amortization, operation costs amor. O(1))
 *
 * Created by shevkunov on 22.10.16.
 */
class LazyMergedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String HEADER_NAME = File.separatorChar + "storage.db";
    private static final String DATA_NAME_PREFIX = File.separatorChar + "storage_";
    private static final String DATA_NAME_SUFFIX = ".db";
    private static final long MININAL_LAZY = 16;

    private boolean open = true;

    private final long cacheSize;
    private final String path;
    private final LazyMergedKeyValueStorageHeader<K, V> header;
    private final LazyMergedKeyValueStorageKeeper<V> keeper;
    private final Cache<K, V> cache;

    LazyMergedKeyValueStorage(LazyMergedKeyValueStorageSerializator<K> keySerializator,
                              LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                              String path, long cacheSize) throws Exception {
        this.cacheSize = cacheSize;
        this.path = path;

        cache = CacheBuilder.newBuilder().maximumSize(this.cacheSize).build();

        File dir = new File(path);
        boolean dirOk = dir.exists() && dir.isDirectory();
        if (!dirOk) {
            throw new FileNotFoundException("No such directory");
        }
        try {
            header = new LazyMergedKeyValueStorageHeader<>(keySerializator, valueSerializator, path + HEADER_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Problems with header-file");
        }

        keeper = new LazyMergedKeyValueStorageKeeper<>(valueSerializator,
                path + DATA_NAME_PREFIX, DATA_NAME_SUFFIX, header.createdByConstructor);
    }

    @Override
    public V read(K key) {
        synchronized (header) {
            checkClosed();
            Long pointer = header.getMap().get(key);
            if (pointer != null) {
                V tryCache = cache.getIfPresent(key);
                if (tryCache == null) {
                    try {
                        V retValue = keeper.read(0, pointer);
                        cache.put(key, retValue);
                        return retValue;
                    } catch (IOException e) {
                        throw new RuntimeException("IO error during reading");
                        // Interface doesn't allow us to throw IOException
                    }
                } else {
                    return tryCache;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (header) {
            checkClosed();
            return header.getMap().containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (header) {
            checkClosed();
            try {
                header.addKey(key, keeper.write(0, value));
                cache.put(key, value);
            } catch (IOException e) {
                throw new RuntimeException("IO error during writing");
                // Interface doesn't allow us to throw IOException
            }
        }
    }

    @Override
    public void delete(K key) {
        synchronized (header) {
            checkClosed();
            if (header.deleteKey(key)) {
                cache.invalidate(key);
                if (lazy()) {
                    try {
                        rebuild();
                    } catch (IOException e) {
                        throw new RuntimeException("IO error during deleting");
                        // Interface doesn't allow us to throw IOException
                    }
                }
            }
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (header) {
            checkClosed();
            return header.getMap().keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (header) {
            checkClosed();
            return header.getMap().size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (header) {
            checkClosed();
            open = false;
            header.write();
            keeper.close();
        }
    }

    private boolean lazy() {
        long lazy = header.getLazyPointers();
        return (lazy >= MININAL_LAZY) && (lazy > header.getFileSize());
    }

    private void rebuild() throws IOException {
        int chacheFile = keeper.newFile();
        for (Map.Entry<K, Long> v : header.getUnsaveMap().entrySet()) {
            V temp = keeper.read(0, v.getValue());
            long wrote = keeper.write(chacheFile, temp);
            v.setValue(wrote);
        }
        keeper.swap(chacheFile, 0);
        keeper.popBack();
        header.setLazyPointers(0);
    }

    private void checkClosed() {
        if (!open) {
            throw new RuntimeException("Storage already closed.");
        }
    }

}
