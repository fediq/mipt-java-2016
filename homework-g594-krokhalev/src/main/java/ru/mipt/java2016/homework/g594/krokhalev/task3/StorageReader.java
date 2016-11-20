package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class StorageReader<K, V> {
    public static final int BOOL_SIZE = 1;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;

    private Class<K> keyClass;
    private Class<V> valueClass;

    public StorageReader(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    private void readAll(InputStream stream, byte[] buff, int offset, int size) throws IOException {
        while (size > 0) {
            int get = stream.read(buff, offset, size);
            offset += get;
            size -= get;
        }
    }

    private void readAll(RandomAccessFile stream, byte[] buff, int offset, int size) throws IOException {
        while (size > 0) {
            int get = stream.read(buff, offset, size);
            offset += get;
            size -= get;
        }
    }

    public int readInt(InputStream stream) throws IOException {
        byte[] intBuff = new byte[INT_SIZE];

        readAll(stream, intBuff, 0, INT_SIZE);

        return (int) Serializer.deserialize(int.class, intBuff);
    }

    public int readInt(RandomAccessFile stream) throws IOException {
        byte[] intBuff = new byte[INT_SIZE];

        readAll(stream, intBuff, 0, INT_SIZE);

        return (int) Serializer.deserialize(int.class, intBuff);
    }

    public long readLong(InputStream stream) throws IOException {
        byte[] longBuff = new byte[LONG_SIZE];

        readAll(stream, longBuff, 0, LONG_SIZE);

        return (long) Serializer.deserialize(long.class, longBuff);
    }

    public byte[] readItem(InputStream stream) throws IOException {
        int size = readInt(stream);
        return readBytes(stream, size);
    }

    public byte[] readItem(RandomAccessFile stream) throws IOException {
        int size = readInt(stream);
        return readBytes(stream, size);
    }

    public byte[] readBlockItem(InputStream stream) throws IOException {
        byte[] sizeBuff = readBytes(stream, INT_SIZE);
        int size = (int) Serializer.deserialize(int.class, sizeBuff);

        byte[] blockItemBuff = new byte[INT_SIZE + size];
        System.arraycopy(sizeBuff, 0, blockItemBuff, 0, INT_SIZE);

        readAll(stream, blockItemBuff, INT_SIZE, size);

        return blockItemBuff;
    }

    public byte[] readBytes(InputStream stream, int size) throws IOException {

        byte[] keyBuff = new byte[size];

        readAll(stream, keyBuff, 0, size);

        return keyBuff;
    }

    public byte[] readBytes(RandomAccessFile stream, int size) throws IOException {

        byte[] keyBuff = new byte[size];

        readAll(stream, keyBuff, 0, size);

        return keyBuff;
    }

    public K readKey(InputStream stream) throws IOException {
        miss(stream, 4);
        return (K) Serializer.deserialize(keyClass, stream);
    }

    public V readValue(InputStream stream) throws IOException {
        miss(stream, 4);
        return (V) Serializer.deserialize(valueClass, stream);
    }

    public void missNext(InputStream stream) throws IOException {
        miss(stream, readInt(stream));
    }

    public void miss(InputStream stream, long count) throws IOException {
        while (count > 0) {
            count -= stream.skip(count);
        }
    }
}
