package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.*;

public class StoragePart<K, V> implements Closeable {
    private File mFile;

    private Class<K> mKeyClass;
    private Class<V> mValueClass;

    private Map<K, Long> mKeys = new HashMap<>();

    StoragePart(File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);
        PositionBufferedInputStream fileStream = new PositionBufferedInputStream(new FileInputStream(mFile));

        while (fileStream.available() > 0) {
            byte[] buff = storageReader.readBlockItem(fileStream);

            InputStream keyBlockStream = new ByteArrayInputStream(buff);
            K key = storageReader.readKey(keyBlockStream);
            keyBlockStream.close();

            mKeys.put(key, fileStream.getPosition());

            storageReader.missNext(fileStream);
        }

        fileStream.close();
    }

    StoragePart(Map<K, V> memTable, File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        PositionBufferedOutputStream thisStream = new PositionBufferedOutputStream(new FileOutputStream(file));

        for (Map.Entry<K, V> iMem : memTable.entrySet()) {

            byte[] buff = Serializer.serialize(iMem.getKey());
            thisStream.write(Serializer.serialize(buff.length));
            thisStream.write(buff);

            mKeys.put(iMem.getKey(), thisStream.getPosition());

            buff = Serializer.serialize(iMem.getValue());
            thisStream.write(Serializer.serialize(buff.length));
            thisStream.write(buff);
        }

        thisStream.close();
    }

    public void copyTo(OutputStream dist) throws IOException {
        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);

        InputStream partStream = new BufferedInputStream(new FileInputStream(mFile));

        while (partStream.available() > 0) {
            byte[] buff = storageReader.readBlockItem(partStream);
            InputStream keyBlockStream = new ByteArrayInputStream(buff);
            K key = storageReader.readKey(keyBlockStream);
            keyBlockStream.close();

            if (containsKey(key)) {

                dist.write(buff);
                dist.write(storageReader.readBlockItem(partStream));

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
