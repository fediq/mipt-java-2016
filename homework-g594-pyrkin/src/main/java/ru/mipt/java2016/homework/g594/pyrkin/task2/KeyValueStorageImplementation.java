package ru.mipt.java2016.homework.g594.pyrkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.SerializerInterface;


import java.io.EOFException;
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
        this.readAllFile();
    }

    @Override
    public V read(K key) throws RuntimeException {
        this.checkClosed();
        return this.hashMap.get(key);
    }

    @Override
    public boolean exists(K key) throws RuntimeException {
        this.checkClosed();
        return this.hashMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) throws RuntimeException {
        this.checkClosed();
        this.hashMap.put(key, value);
    }

    @Override
    public void delete(K key) throws RuntimeException {
        this.checkClosed();
        this.hashMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() throws RuntimeException {
        this.checkClosed();
        return this.hashMap.keySet().iterator();
    }

    @Override
    public int size() throws RuntimeException {
        this.checkClosed();
        return this.hashMap.size();
    }

    @Override
    public void close() throws RuntimeException, IOException {
        this.checkClosed();
        isClosed = true;
        this.writeStorageToFile();
    }

    private int readFieldSize() throws IOException {
        return this.fileWorker.read();
    }

    private ByteBuffer readField(int size) throws IOException {
        return this.fileWorker.read(size);
    }

    private void readAllFile() throws IOException {
        while (true) {
            int size;
            try {
                size = this.readFieldSize();
            } catch (EOFException e) {
                break;
            }
            K key = this.keySerializer.deserialize(this.readField(size));
            size = this.readFieldSize();
            V value = this.valueSerializer.deserialize(this.readField(size));
            this.hashMap.put(key, value);
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("storage closed");
        }
    }

    private void writeField(K key, V value) throws IOException {
        this.fileWorker.write(this.keySerializer.sizeOfSerialize(key));
        this.fileWorker.write(this.keySerializer.serialize(key));

        this.fileWorker.write(this.valueSerializer.sizeOfSerialize(value));
        this.fileWorker.write(this.valueSerializer.serialize(value));
    }

    private void writeStorageToFile() throws IOException {
        this.fileWorker.clear();
        for (Map.Entry<K, V> entry : this.hashMap.entrySet()) {
            this.writeField(entry.getKey(), entry.getValue());
        }
    }
}
