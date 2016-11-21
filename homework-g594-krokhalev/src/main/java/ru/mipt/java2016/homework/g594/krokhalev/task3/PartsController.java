package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class PartsController<K, V> implements Closeable {
    private static final int BASE_PART_SIZE = 100;
    private static final int LEVEL_INCREASE = 10;

    private final StorageReader<K, V> mStorageReader;

    private Map<K, V> mLevel0 = new HashMap<K, V>();
    private LinkedList<Level<K, V>> mLevels = new LinkedList<Level<K, V>>();
    private Part<K, V> mainPart;

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
            createFromPart(tmpPart);
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
            addMemPart(new Part<K, V>(mWorkDirectory, mLevel0, mStorageReader));
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

    private void addMemPart(Part<K, V> part) throws IOException {
        int i = 0;
        int currentCapacity = BASE_PART_SIZE * LEVEL_INCREASE;
        boolean added = false;
        for (Level<K, V> iLevel : mLevels) {
            if (iLevel.getCapacity() > currentCapacity) {
                Level<K, V> level = new Level<K, V>(mWorkDirectory, mStorageReader, currentCapacity);
                level.addPart(part);
                mLevels.add(i, level);

                added = true;
                break;
            } else {
                if (iLevel.getSize() + part.getSize() > iLevel.getCapacity()) {
                    Part<K, V> tmpPart = iLevel.merge();
                    iLevel.addPart(part);
                    part = tmpPart;
                } else {
                    iLevel.addPart(part);

                    added = true;
                    break;
                }
            }
            i++;
            currentCapacity *= LEVEL_INCREASE;
        }
        if (!added) {
            mLevels.addLast(new Level<K, V>(mWorkDirectory, mStorageReader, currentCapacity));
            mLevels.getLast().addPart(part);
        }
    }

    private void createFromPart(Part<K, V> part) throws FileNotFoundException {
        mLevels.clear();

        int currentSize = BASE_PART_SIZE * LEVEL_INCREASE;
        while (currentSize < part.getSize()) {
            currentSize *= LEVEL_INCREASE;
        }
        mLevels.addFirst(new Level<K, V>(mWorkDirectory, mStorageReader, currentSize));
        mLevels.getFirst().addPart(part);
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
