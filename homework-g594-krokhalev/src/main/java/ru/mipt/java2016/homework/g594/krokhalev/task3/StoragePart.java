package ru.mipt.java2016.homework.g594.krokhalev.task3;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class StoragePart<K, V> implements Closeable {
    private int mCapacity;
    private File mFile;

    private Class<K> mKeyClass;
    private Class<V> mValueClass;

    private Map<K, Long> mKeys = new HashMap<>();

    StoragePart(File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;
        mCapacity = KrokhalevsKeyValueStorage.CACHE_SIZE;

        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);
        PositionBufferedInputStream fileStream = new PositionBufferedInputStream(new FileInputStream(mFile));

        int countKeys = 0;
        while (fileStream.available() > 0) {
            byte[] buff = storageReader.readBlockItem(fileStream);

            InputStream keyBlockStream = new ByteArrayInputStream(buff);
            K key = storageReader.readKey(keyBlockStream);
            keyBlockStream.close();

            mKeys.put(key, fileStream.getPosition());

            storageReader.missNext(fileStream);

            countKeys++;
        }

        while (countKeys > mCapacity) {
            mCapacity *= KrokhalevsKeyValueStorage.PARTS_INCREASE;
        }

        fileStream.close();
    }

    StoragePart(Map<K, V> memTable, File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;
        mCapacity = KrokhalevsKeyValueStorage.CACHE_SIZE;

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

    StoragePart(StoragePart<K, V> part1, StoragePart<K, V> part2, File file) throws IOException {
        if (part1.getCapacity() != part2.getCapacity()) {
            throw new RuntimeException("Internal error: Can not merge parts with different sizes");
        }

        mKeyClass = part1.mKeyClass;
        mValueClass = part1.mValueClass;
        mFile = file;
        mCapacity = part1.getCapacity();

        if (mCapacity < part1.getSize() + part2.getSize()) {
            mCapacity *= KrokhalevsKeyValueStorage.PARTS_INCREASE;
        }

        PositionBufferedOutputStream thisStream  = new PositionBufferedOutputStream(new FileOutputStream(file));
        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);

        StoragePart<K, V>[] parts = new StoragePart[]{part1, part2};

        for (int i = 0; i < 2; ++i) {
            InputStream partStream = new BufferedInputStream(new FileInputStream(parts[i].getFile()));

            while (partStream.available() > 0) {
                byte[] buff = storageReader.readBlockItem(partStream);
                InputStream keyBlockStream = new ByteArrayInputStream(buff);
                K key = storageReader.readKey(keyBlockStream);
                keyBlockStream.close();

                if (parts[i].containsKey(key)) {
                    thisStream.write(buff);

                    mKeys.put(key, thisStream.getPosition());

                    thisStream.write(storageReader.readBlockItem(partStream));
                } else {
                    storageReader.missNext(partStream);
                }
            }
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
        if (!containsKey(key)) {
            return null;
        }

        BufferedInputStream thisStream = new BufferedInputStream(new FileInputStream(mFile));
        StorageReader<K, V> storageReader = new StorageReader<K, V>(mKeyClass, mValueClass);

        storageReader.miss(thisStream, mKeys.get(key));
        V value = storageReader.readValue(thisStream);

        thisStream.close();

        return value;
    }

    public void removeKey(K key) {
        mKeys.remove(key);
    }

    public long getKeyOffset(K key) {
        return mKeys.get(key);
    }

    public int getSize() {
        return mKeys.size();
    }

    public int getCapacity() {
        return mCapacity;
    }

    public File getFile() {
        return mFile;
    }

    public boolean renameFileTo(File newFile) {
        if (!mFile.renameTo(newFile)) {
            return false;
        }
        mFile = newFile;
        return true;
    }

    @Override
    public void close() throws IOException {
        mKeys.clear();
        if (!mFile.delete()) {
            throw new IOException("Can not delete file");
        }
    }
}
