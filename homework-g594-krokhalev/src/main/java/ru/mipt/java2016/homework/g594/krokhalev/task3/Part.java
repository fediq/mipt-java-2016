package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Part<K, V> implements Closeable {
    private final StorageReader<K, V> mStorageReader;

    private final File mStorageFile;
    private final File mStorageTable;

    private Map<K, Long> mKeysPositions = new HashMap<K, Long>();
    private RandomAccessFile mRAFile;

    Part(File storageFile,
         File storageTable,
         StorageReader<K, V> storageReader,
         boolean restore) throws IOException {

        mStorageReader = storageReader;
        mStorageFile = storageFile;
        mStorageTable = storageTable;

        if (restore) {
            try (BufferedInputStream tableStream = new BufferedInputStream(new FileInputStream(storageTable))) {
                while (tableStream.available() > 0) {
                    K key = mStorageReader.readKey(tableStream);
                    Long pos = mStorageReader.readLong(tableStream);

                    mKeysPositions.put(key, pos);
                }
            }
        }
        mRAFile = new RandomAccessFile(storageFile, "rw");
    }

    int getSize() {
        return mKeysPositions.size();
    }

    boolean exists(K key) {
        return mKeysPositions.containsKey(key);
    }

    boolean remove(K key) {
        return mKeysPositions.remove(key) != null;
    }

    V read(K key) throws IOException {
        return find(key);
    }

    boolean write(K key, V value) throws IOException {
        mRAFile.seek(mRAFile.length());

        Long exists = mKeysPositions.put(key, mRAFile.getFilePointer());
        mStorageReader.writeValue(value, mRAFile);
        return exists != null;
    }

    @Override
    public void close() throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mStorageTable))) {

            for (Map.Entry<K, Long> iKeyPosition : mKeysPositions.entrySet()) {
                mStorageReader.writeKey(iKeyPosition.getKey(), bos);
                mStorageReader.writeLong(iKeyPosition.getValue(), bos);
            }
            mRAFile.close();

        }
    }

    Set<K> getKeys() {
        return mKeysPositions.keySet();
    }

    private V find(K key) throws IOException {
        Long offset = mKeysPositions.get(key);
        V value = null;

        if (offset != null) {
            mRAFile.seek(offset);

            value = mStorageReader.readValue(mRAFile);
        }
        return value;
    }
}
