package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.*;

public class StoragePart<K, V> implements Closeable {
    private File mFile;

    private Class<K> mKeyClass;
    private Class<V> mValueClass;

    private Map<K, Long> mKeys = new HashMap<>();

    StoragePart(File file, File tableFile, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);
        BufferedInputStream tableStream = new BufferedInputStream(new FileInputStream(tableFile));

        while (tableStream.available() > 0) {
            K key = storageReader.readKey(tableStream);
            mKeys.put(key, storageReader.readLong(tableStream));
        }

        tableStream.close();
    }

    StoragePart(Map<K, V> memTable, File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        PositionBufferedOutputStream thisStream = new PositionBufferedOutputStream(new FileOutputStream(file));

        byte[] buff;
        for (Map.Entry<K, V> iMem : memTable.entrySet()) {

            buff = Serializer.serialize(iMem.getKey());
            thisStream.write(Serializer.serialize(buff.length));
            thisStream.write(buff);

            mKeys.put(iMem.getKey(), thisStream.getPosition());

            buff = Serializer.serialize(iMem.getValue());
            thisStream.write(Serializer.serialize(buff.length));
            thisStream.write(buff);
        }

        thisStream.close();
    }

    public void copyTo(PositionBufferedOutputStream storage, OutputStream storageTable) throws IOException {
        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);

        InputStream partStream = new BufferedInputStream(new FileInputStream(mFile));

        byte[] buff;
        while (partStream.available() > 0) {
            buff = storageReader.readBlockItem(partStream);
            InputStream keyBlockStream = new ByteArrayInputStream(buff);
            K key = storageReader.readKey(keyBlockStream);
            keyBlockStream.close();

            if (containsKey(key)) {

                storage.write(buff);

                storageTable.write(buff);
                storageTable.write(Serializer.serialize(storage.getPosition()));

                storage.write(storageReader.readBlockItem(partStream));

            } else {
                storageReader.missNext(partStream);
            }
        }

        partStream.close();
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
            BufferedInputStream thisStream = new BufferedInputStream(new FileInputStream(mFile));
            StorageReader<K, V> storageReader = new StorageReader<K, V>(mKeyClass, mValueClass);

            storageReader.miss(thisStream, offset);
            value = storageReader.readValue(thisStream);

            thisStream.close();
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
