package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;
import java.util.*;

public class StoragePart<K, V> implements Closeable {
    private File mFile;

    private Class<K> mKeyClass;
    private Class<V> mValueClass;

    private LinkedHashMap<K, Location> mKeys = new LinkedHashMap<K, Location>();

    StoragePart(File file, File tableFile, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);
        BufferedInputStream tableStream = new PositionBufferedInputStream(new FileInputStream(tableFile));

        while (tableStream.available() > 0) {
            K key = storageReader.readKey(tableStream);
            mKeys.put(key, new Location(storageReader.readLong(tableStream), storageReader.readInt(tableStream)));
        }

        tableStream.close();
    }

    StoragePart(Map<K, V> memTable, File file, Class<K> keyClass, Class<V> valueClass) throws IOException {
        mFile = file;
        mKeyClass = keyClass;
        mValueClass = valueClass;

        PositionBufferedOutputStream thisStream = new PositionBufferedOutputStream(new FileOutputStream(file));

        for (Map.Entry<K, V> iMem : memTable.entrySet()) {
            Serializer.serialize(iMem.getKey(), thisStream);
            long pos = thisStream.getPosition();
            Serializer.serialize(iMem.getValue(), thisStream);

            mKeys.put(iMem.getKey(), new Location(pos, (int) (thisStream.getPosition() - pos)));
        }

        thisStream.close();
    }

    public void copyTo(PositionBufferedOutputStream storage, OutputStream storageTable) throws IOException {
        StorageReader<K, V> storageReader = new StorageReader<>(mKeyClass, mValueClass);

        PositionBufferedInputStream partStream = new PositionBufferedInputStream(new FileInputStream(mFile));

        for (Map.Entry<K, Location> iKey : mKeys.entrySet()) {
            byte[] buffKey = Serializer.serialize(iKey.getKey());
            storageTable.write(buffKey);
            storage.write(buffKey);
            Serializer.serialize(storage.getPosition(), storageTable);
            Serializer.serialize(iKey.getValue().getLen(), storageTable);

            storageReader.miss(partStream, iKey.getValue().getPos() - partStream.getPosition());
            storage.write(storageReader.readBytes(partStream, iKey.getValue().getLen()));
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
        Location offset = mKeys.get(key);
        V value = null;
        if (offset != null) {
            BufferedInputStream thisStream = new PositionBufferedInputStream(new FileInputStream(mFile));
            StorageReader<K, V> storageReader = new StorageReader<K, V>(mKeyClass, mValueClass);

            storageReader.miss(thisStream, offset.getPos());
            value = storageReader.readValue(thisStream);

            thisStream.close();
        }

        return value;
    }

    public boolean removeKey(K key) {
        Location old = mKeys.remove(key);
        return old != null;
    }

    @Override
    public void close() throws IOException {
        mKeys.clear();
        if (!mFile.delete()) {
            throw new IOException("Can not delete file");
        }
    }
}
