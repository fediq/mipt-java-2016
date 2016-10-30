package ru.mipt.java2016.homework.g594.pyrkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.SerializerInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * binary hash-map KeyValueStorage
 * Created by randan on 10/30/16.
 */
public class KeyValueStorageImplementation<K, V> implements KeyValueStorage<K, V>{

    private final FileWorker fileWorker;

    private final HashMap<K, V> hashMap = new HashMap<>();

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private boolean isClosed = false;

    public KeyValueStorageImplementation(String directoryPath,
                                         SerializerInterface<K> keySerializer,
                                         SerializerInterface<V> valueSerializer)
            throws  IOException{
        fileWorker = new FileWorker(directoryPath, "storage.db");
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;


    }

    @Override
    public V read(K key) {
        return null;
    }

    @Override
    public boolean exists(K key) {
        return false;
    }

    @Override
    public void write(K key, V value) {

    }

    @Override
    public void delete(K key) {

    }

    @Override
    public Iterator<K> readKeys() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    private int readFieldSize() throws  IOException {
        return this.fileWorker.read();
    }

    private ByteBuffer readField(int size) throws  IOException {
        return this.fileWorker.read(size);
    }

    private void readAllFile() throws IOException {

    }
}
