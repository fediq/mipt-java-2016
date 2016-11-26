package ru.mipt.java2016.homework.g594.pyrkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.SerializerInterface;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * binary hash-map KeyValueStorage
 * Created by randan on 10/30/16.
 */
public class KeyValueStorageImplementation<K, V> implements KeyValueStorage<K, V> {

    private final FileWorker fileWorker;

    private final HashMap<K, V> hashMap = new HashMap<>();

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private boolean isClosed = false;

    public KeyValueStorageImplementation(String directoryPath,
                                         SerializerInterface<K> keySerializer,
                                         SerializerInterface<V> valueSerializer)
            throws IOException {
        fileWorker = new FileWorker(directoryPath, "storage.db");
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        readAllFile();
    }

    @Override
    public V read(K key) throws RuntimeException {
        checkClosed();
        return hashMap.get(key);
    }

    @Override
    public boolean exists(K key) throws RuntimeException {
        checkClosed();
        return hashMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) throws RuntimeException {
        checkClosed();
        hashMap.put(key, value);
    }

    @Override
    public void delete(K key) throws RuntimeException {
        checkClosed();
        hashMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() throws RuntimeException {
        checkClosed();
        return hashMap.keySet().iterator();
    }

    @Override
    public int size() throws RuntimeException {
        checkClosed();
        return hashMap.size();
    }

    @Override
    public void close() throws RuntimeException, IOException {
        checkClosed();
        isClosed = true;
        writeStorageToFile();
        fileWorker.close();
    }

    private int readFieldSize() throws IOException {
        return fileWorker.read();
    }

    private ByteBuffer readField(int size) throws IOException {
        return fileWorker.read(size);
    }

    private void readAllFile() throws IOException {
        while (true) {
            int size;
            size = readFieldSize();
            if (size <= 0) {
                break;
            }

            K key = keySerializer.deserialize(readField(size));
            size = readFieldSize();
            V value = valueSerializer.deserialize(readField(size));
            hashMap.put(key, value);
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("storage closed");
        }
    }

    private void writeField(K key, V value) throws IOException {
        fileWorker.write(keySerializer.sizeOfSerialize(key));
        fileWorker.write(keySerializer.serialize(key));

        fileWorker.write(valueSerializer.sizeOfSerialize(value));
        fileWorker.write(valueSerializer.serialize(value));
    }

    private void writeStorageToFile() throws IOException {
        fileWorker.clear();
        for (Map.Entry<K, V> entry : hashMap.entrySet()) {
            writeField(entry.getKey(), entry.getValue());
        }
    }
}
