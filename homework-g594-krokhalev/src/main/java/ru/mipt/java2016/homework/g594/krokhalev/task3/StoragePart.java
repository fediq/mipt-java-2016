package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.*;

public class StoragePart<K, V> implements Closeable {
    private File mFile;

    private StorageReader<K, V> mStorageReader;

    private Map<K, Long> mKeys = new LinkedHashMap<K, Long>();

    StoragePart(File file, File tableFile, StorageReader<K, V> storageReader) throws IOException {
        mFile = file;
        mStorageReader = storageReader;

        try (BufferedInputStream tableStream = new BufferedInputStream(new FileInputStream(tableFile))) {
            while (tableStream.available() > 0) {
                K key = storageReader.readKey(tableStream);
                mKeys.put(key, storageReader.readLong(tableStream));
            }
        }
    }

    StoragePart(Map<K, V> memTable, File file, StorageReader<K, V> storageReader) throws IOException {
        mFile = file;
        mStorageReader = storageReader;

        try (PositionBufferedOutputStream thisStream = new PositionBufferedOutputStream(new FileOutputStream(file))) {
            for (Map.Entry<K, V> iMem : memTable.entrySet()) {
                mKeys.put(iMem.getKey(), thisStream.getPosition());

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    storageReader.writeValue(iMem.getValue(), baos);

                    storageReader.writeInt(baos.size(), thisStream);
                    thisStream.write(baos.toByteArray());
                }
            }
        }
    }

    public void copyTo(PositionBufferedOutputStream storage, OutputStream storageTable) throws IOException {

        try (PositionBufferedInputStream partStream = new PositionBufferedInputStream(new FileInputStream(mFile))) {

            for (Map.Entry<K, Long> iKey : mKeys.entrySet()) {
                mStorageReader.skip(iKey.getValue() - partStream.getPosition(), partStream);

                iKey.setValue(storage.getPosition());

                int size = mStorageReader.readInt(partStream);
                byte[] buff = new byte[size];
                mStorageReader.read(buff, partStream);

                mStorageReader.writeInt(size, storage);
                storage.write(buff);

                mStorageReader.writeKey(iKey.getKey(), storageTable);
                mStorageReader.writeLong(iKey.getValue(), storageTable);
            }
        }
    }

    public Set<K> getKeys() {
        return mKeys.keySet();
    }

    public boolean containsKey(K key) {
        return mKeys.containsKey(key);
    }

    public V getValue(K key) throws IOException {
        Long offset = mKeys.get(key);
        V value = null;
        if (offset != null) {
            try (FileInputStream fis = new FileInputStream(mFile)) {
                mStorageReader.skip(offset + 4, fis);

                try (BufferedInputStream thisStream = new BufferedInputStream(fis)) {

                    value = mStorageReader.readValue(thisStream);

                }
            }
        }

        return value;
    }

    public void removeKey(K key) {
        mKeys.remove(key);
    }

    @Override
    public void close() throws IOException {
        mKeys.clear();
        if (!mFile.delete()) {
            throw new IOException("Can not delete file");
        }
    }
}
