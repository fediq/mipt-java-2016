package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class PartsController<K, V> implements Closeable {
    private static final int BASE_PART_SIZE = 100;
    private static final int LEVEL_INCREASE = 10;

    private final StorageReader<K, V> mStorageReader;

    private Map<K, V> mLevel0 = new HashMap<K, V>();
    private LinkedList<Level<K, V>> mLevels = new LinkedList<>();

    private final File mStorageFile;
    private final File mStorageTable;
    private final File mWorkDirectory;

    private static long partId = 1;

    static String getNextPartName() {
        return "Part_" + partId++;
    }

    public PartsController(File workDirectory,
                           File storageFile,
                           File storageTable,
                           Set<K> keys,
                           StorageReader<K, V> storageReader,
                           boolean restore) throws IOException {

        if (restore) {
            if (!workDirectory.exists() || !workDirectory.isDirectory()) {
                throw new RuntimeException("\"" + workDirectory.getAbsolutePath() + "\" "
                        + "do not exist or not a directory");
            }

            if (!storageFile.exists() || storageFile.isDirectory()) {
                throw new RuntimeException("\"" + storageFile.getAbsolutePath() + "\" "
                        + "do not exist or a directory");
            }

            if (!storageFile.exists() || storageFile.isDirectory()) {
                throw new RuntimeException("\"" + storageTable.getAbsolutePath() + "\" "
                        + "do not exist or a directory");
            }
        }

        mStorageFile = storageFile;
        mStorageTable = storageTable;
        mWorkDirectory = workDirectory;

        mStorageReader = storageReader;
        if (restore) {
            Part<K, V> tmpPart = new Part<K, V>(mWorkDirectory, storageFile, storageTable, storageReader);
            addPart(tmpPart);
            mLevels.getFirst().getKeys(keys);
        }

    }

    V read(K key) throws IOException {
        V value = mLevel0.get(key);
        if (value == null) {
            for (Level<K, V> iLevel : mLevels) {
                value = iLevel.read(key);
                if (value != null) {
                    break;
                }
            }
        }
        return value;
    }

    boolean exists(K key) throws IOException {
        boolean exists = mLevel0.containsKey(key);
        if (!exists) {
            for (Level<K, V> iLevel : mLevels) {
                exists = iLevel.exists(key);
                if (exists) {
                    break;
                }
            }
        }
        return exists;
    }

    boolean write(K key, V value) throws IOException {
        boolean exists = (mLevel0.put(key, value) != null);
        if (!exists) {
            for (Level<K, V> iLevel : mLevels) {
                exists = iLevel.remove(key);
                if (exists) {
                    break;
                }
            }
        }

        if (mLevel0.size() >= BASE_PART_SIZE) {
            addPart(new Part<K, V>(mWorkDirectory, mLevel0, mStorageReader));
        }

        return exists;
    }

    boolean remove(K key) throws IOException {
        boolean exists = (mLevel0.remove(key) != null);
        if (!exists) {
            for (Level<K, V> iLevel : mLevels) {
                exists = iLevel.remove(key);
                if (exists) {
                    break;
                }
            }
        }
        return exists;
    }

    int size() {
        int size = mLevel0.size();
        for (Level<K, V> iLevel : mLevels) {
            size += iLevel.getSize();
        }
        return size;
    }

    private void addPart(Part<K, V> part) throws IOException {
        boolean added = false;
        while (!added && mLevels.size() > 0) {
            Level<K, V> level = mLevels.getFirst();
            if (level.addPart(part) > level.getCapacity()) {
                part = level.merge();
                mLevels.remove(0);
            } else {
                added = true;
            }
        }
        if (!added) {
            int currentSize = BASE_PART_SIZE * LEVEL_INCREASE;
            while (currentSize < part.getSize()) {
                currentSize *= LEVEL_INCREASE;
            }
            mLevels.add(new Level<K, V>(mWorkDirectory, mStorageReader, currentSize));
            mLevels.getFirst().addPart(part);
        }
    }

    @Override
    public void close() throws IOException {
        Part<K, V> part = new Part<K, V>(mWorkDirectory, mStorageReader);
        if (mLevel0.size() > 0) {
            part.append(new Part<K, V>(mWorkDirectory, mLevel0, mStorageReader));
        }
        for (Level<K, V> iLevel : mLevels) {
            iLevel.merge(part);
        }
        part.save(mStorageFile, mStorageTable);
    }
}
