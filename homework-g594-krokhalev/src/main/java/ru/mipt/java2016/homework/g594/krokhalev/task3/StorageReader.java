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
        byte[] keyByte = mKeySerializer.serialize(key);
        writeInt(keyByte.length, stream);
        stream.write(keyByte);
    }

    void writeValue(V value, OutputStream stream) throws IOException {
        byte[] keyByte = mValueSerializer.serialize(value);
        writeInt(keyByte.length, stream);
        stream.write(keyByte);
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
        skip(4, stream);
        return (K) mKeySerializer.deserialize(stream);
    }

    V readValue(InputStream stream) throws IOException {
        skip(4, stream);
        return (V) mValueSerializer.deserialize(stream);
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
}
