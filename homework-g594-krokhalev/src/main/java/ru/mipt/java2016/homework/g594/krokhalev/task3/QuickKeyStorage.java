package ru.mipt.java2016.homework.g594.krokhalev.task3;

import com.google.common.cache.CacheBuilder;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import com.google.common.cache.Cache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class QuickKeyStorage<K, V> implements KeyValueStorage<K, V> {

    private static final String STORAGE_NAME = "storage.db";
    private static final String STORAGE_TABLE_NAME = "storageTable.db";

    private final File mWorkDirectory;
    private File mPartsDirectory;

    private Part<K, V> mMainPart;
    private Cache<K, V> mCache;

    private boolean isClosed = false;

    public QuickKeyStorage(String workDirectoryPath,
                           SerializationStrategy<K> keySerializer,
                           SerializationStrategy<V> valueSerializer,
                           int cacheSize) {

        mWorkDirectory = new File(workDirectoryPath);

        if (!mWorkDirectory.exists() || !mWorkDirectory.isDirectory()) {
            throw new RuntimeException("\"" + mWorkDirectory.getAbsolutePath() + "\" "
                    + "do not exist or not a directory");
        }

        if (cacheSize <= 0) {
            throw new RuntimeException("Cache size size should be natural number");
        }

        mCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
        StorageReader<K, V> storageReader = new StorageReader<K, V>(keySerializer, valueSerializer);

        try {
            File storageFile = findFile(STORAGE_NAME);
            File storageTable = findFile(STORAGE_TABLE_NAME);
            mPartsDirectory = createPartsDirectory();

            if (storageFile == null || storageTable == null) {
                if (storageFile == null) {
                    storageFile = new File(mWorkDirectory.getAbsolutePath() + File.separatorChar + STORAGE_NAME);
                }
                if (storageTable == null) {
                    storageTable = new File(mWorkDirectory.getAbsolutePath() + File.separatorChar + STORAGE_TABLE_NAME);
                }

                mMainPart = new Part<K, V>(mPartsDirectory, storageFile, storageTable, storageReader, false);

            } else {
                mMainPart = new Part<K, V>(mPartsDirectory, storageFile, storageTable, storageReader, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V read(K key) {
        checkClosed();
        V value = mCache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        try {
            value = mMainPart.read(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (value != null) {
            mCache.put(key, value);
        }
        return value;
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        return mMainPart.exists(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        mCache.put(key, value);
        try {
            mMainPart.write(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        checkClosed();
        mCache.invalidate(key);
        mMainPart.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return mMainPart.getKeys().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return mMainPart.getSize();
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        mMainPart.close();
        if (!mPartsDirectory.delete()) {
            throw new RuntimeException("Can not delete part directory");
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Storage closed");
        }
    }

    private File createPartsDirectory() throws IOException {
        String name = String.valueOf(mWorkDirectory.getAbsoluteFile()) +
                File.separatorChar +
                "Parts";
        File partsDirectory = new File(name);
        if (!partsDirectory.mkdir()) {
            throw new RuntimeException("Can not create directory for parts");
        }
        return partsDirectory;
    }

    private File findFile(String fileName) {
        File file = null;
        for (File item : mWorkDirectory.listFiles()) {
            if (item.isFile() && item.getName().equals(fileName)) {
                file = item;
                break;
            }
        }
        return file;
    }
}
