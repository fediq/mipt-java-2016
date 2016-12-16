package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.*;

class StorageReader<K, V> {
    private final SerializationStrategy<K> mKeySerializer;
    private final SerializationStrategy<V> mValueSerializer;

    StorageReader(SerializationStrategy<K> keySerializer, SerializationStrategy<V> valueSerializer) {
        mKeySerializer = keySerializer;
        mValueSerializer = valueSerializer;
    }

    void writeKey(K key, OutputStream stream) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);
        mKeySerializer.serialize(dos, key);
    }

    void writeValue(V value, OutputStream stream) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);
        mValueSerializer.serialize(dos, value);
    }

    void writeInt(int val, OutputStream stream) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeInt(val);
    }

    void writeLong(long val, OutputStream stream) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeLong(val);
    }

    K readKey(InputStream stream) throws IOException {
        DataInputStream dis = new DataInputStream(stream);
        return mKeySerializer.deserialize(dis);
    }

    V readValue(InputStream stream) throws IOException {
        DataInputStream dis = new DataInputStream(stream);
        return mValueSerializer.deserialize(dis);
    }

    void skip(long len, InputStream stream) throws IOException {
        while (len > 0) {
            long skiped = stream.skip(len);
            if (skiped > 0) {
                len -= skiped;
            } else {
                throw new RuntimeException("Bad stream");
            }
        }
    }

//    void skipItem(InputStream stream) throws IOException {
//        int len = readInt(stream);
//        skip(len, stream);
//    }

    void read(byte[] dist, InputStream stream) throws IOException {
        read(dist, 0, dist.length, stream);
    }

    void read(byte[] dist, int offset, int len, InputStream stream) throws IOException {
        while (len > 0) {
            int readed = stream.read(dist, offset, len);

            if (readed > 0) {
                len -= readed;
                offset += readed;
            } else {
                throw new RuntimeException("Bad stream");
            }
        }
    }

    long readLong(InputStream stream) throws IOException {
        DataInputStream dis = new DataInputStream(stream);
        return dis.readLong();
    }

    int readInt(InputStream stream) throws IOException {
        DataInputStream dis = new DataInputStream(stream);
        return dis.readInt();
    }

    void writeKey(K key, RandomAccessFile file) throws IOException {
        mKeySerializer.serialize(file, key);
    }

    void writeValue(V value, RandomAccessFile file) throws IOException {
        mValueSerializer.serialize(file, value);
    }

    void writeInt(int val, RandomAccessFile file) throws IOException {
        file.writeInt(val);
    }

    void writeLong(long val, RandomAccessFile file) throws IOException {
        file.writeLong(val);
    }

    K readKey(RandomAccessFile file) throws IOException {
        return mKeySerializer.deserialize(file);
    }

    V readValue(RandomAccessFile file) throws IOException {
        return mValueSerializer.deserialize(file);
    }

    void skip(long len, RandomAccessFile file) throws IOException {
        file.seek(file.getFilePointer() + len);
    }

    void read(byte[] dist, RandomAccessFile file) throws IOException {
        read(dist, 0, dist.length, file);
    }

    void read(byte[] dist, int offset, int len, RandomAccessFile file) throws IOException {
        file.read(dist, offset, len);
    }

    long readLong(RandomAccessFile file) throws IOException {
        return file.readLong();
    }

    int readInt(RandomAccessFile file) throws IOException {
        return file.readInt();
    }
}
