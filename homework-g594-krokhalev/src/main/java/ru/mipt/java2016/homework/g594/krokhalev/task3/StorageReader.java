package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wheeltune on 14.11.16.
 */
public class StorageReadWriter<K, V> {
    private Class<K> keyClass;
    private Class<V> valueClass;

    StorageReadWriter(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public boolean readBool(RandomAccessFile file) throws IOException {
        byte[] boolBuff = new byte[1];
        file.read(boolBuff);
        return (boolean) Serializer.deserialize(boolean.class, boolBuff);
    }

    public static byte[] readBytes(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        byte[] keyBuff = new byte[size];
        file.read(keyBuff);

        return keyBuff;
    }

    public static int readInt(RandomAccessFile file) throws IOException {
        byte[] intBuff = new byte[4];
        file.read(intBuff);
        return (int) Serializer.deserialize(int.class, intBuff);
    }

    public static long readLong(RandomAccessFile file) throws IOException {
        byte[] longBuff = new byte[8];
        file.read(longBuff);
        return (long) Serializer.deserialize(long.class, longBuff);
    }

    public K readKey(RandomAccessFile file) throws IOException {
        return (K) Serializer.deserialize(keyClass, readBytes(file));
    }

    public V readValue(RandomAccessFile file) throws IOException {
        return (V) Serializer.deserialize(valueClass, readBytes(file));
    }

    public static void missNext(RandomAccessFile file) throws IOException {
        int size = readInt(file);

        file.seek(file.getFilePointer() + size);
    }

    public void writeKey(RandomAccessFile file, K key) throws IOException {
        byte[] keyBuff = null;

        keyBuff = Serializer.serialize(key);

        file.write(Serializer.serialize(false));
        file.write(Serializer.serialize(keyBuff.length));
        file.write(keyBuff);
    }
}
