package ru.mipt.java2016.homework.g594.krokhalev.TestStorage;

import com.google.common.cache.CacheBuilder;
import com.sun.istack.internal.NotNull;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import com.google.common.cache.Cache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class QuickKeyStorage<K, V> implements KeyValueStorage<K, V> {

    private final static int BASE_PART_SIZE = 100;
    private final static int LEVEL_INCREASE = 10;
    private final static String STORAGE_NAME = "storage.db";
    private final static String STORAGE_TABLE_NAME = "storageTable.db";
    private final File mWorkDirectory;
    private File mPartsDirectory;

    private PartsController<K, V> mPartsController;
    private Cache<K, V> mCache;
    private Set<K> mKeys = new HashSet<K>();

    private boolean isClosed = false;

    public QuickKeyStorage(@NotNull String workDirectoryPath,
                           @NotNull SerializationStrategy<K> keySerializer,
                           @NotNull SerializationStrategy<V> valueSerializer,
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

                mPartsController = new PartsController<K, V>(mPartsDirectory, storageFile, storageTable, mKeys,
                        storageReader, BASE_PART_SIZE, LEVEL_INCREASE, false);

            } else {
                mPartsController = new PartsController<K, V>(mPartsDirectory, storageFile, storageTable, mKeys,
                        storageReader, BASE_PART_SIZE, LEVEL_INCREASE, true);
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
            value = mPartsController.read(key);
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
        return mKeys.contains(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        mCache.put(key, value);
        mKeys.add(key);
        try {
            mPartsController.write(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K key) {
        checkClosed();
        mCache.invalidate(key);
        boolean exists = mKeys.remove(key);
        if (exists) {
            try {
                mPartsController.remove(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return mKeys.iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return mPartsController.size();
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        mPartsController.close();
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
