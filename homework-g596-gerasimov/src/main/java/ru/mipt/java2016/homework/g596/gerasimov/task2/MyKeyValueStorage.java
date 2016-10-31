package ru.mipt.java2016.homework.g596.gerasimov.task2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.ISerializer;

/**
 * Created by geras-artem on 30.10.16.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final HashMap<K, V> storage = new HashMap<>();

    private final FileIO fileIO;

    private final ISerializer<K> keySerializer;

    private final ISerializer<V> valueSerializer;

    private boolean isClosed = false;

    public MyKeyValueStorage(String directoryPath, ISerializer<K> keySerializer,
            ISerializer<V> valueSerializer) throws IOException {
        this.fileIO = new FileIO(directoryPath, "storage.db");
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.readFile();
    }

    @Override
    public V read(K key) throws RuntimeException {
        checkClosed();
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) throws RuntimeException {
        checkClosed();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) throws RuntimeException {
        checkClosed();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) throws RuntimeException {
        checkClosed();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() throws RuntimeException {
        checkClosed();
        return storage.keySet().iterator();
    }

    @Override
    public int size() throws RuntimeException {
        checkClosed();
        return storage.size();
    }

    @Override
    public void close() throws IOException {
        writeToFile();
        fileIO.fileClose();
        storage.clear();
        isClosed = true;
    }

    private void readFile() throws IOException {
        int sizeOfKey;
        int sizeOfValue;
        while (fileIO.fileGetFilePointer() < fileIO.fileLength()) {
            sizeOfKey = fileIO.readSize();
            K key = keySerializer.deserialize(fileIO.readField(sizeOfKey));
            sizeOfValue = fileIO.readSize();
            V value = valueSerializer.deserialize(fileIO.readField(sizeOfValue));
            storage.put(key, value);
        }
        if (fileIO.fileGetFilePointer() != fileIO.fileLength()) {
            throw new IOException("Wrong format of storage.db");
        }
    }

    private void writeToFile() throws IOException {
        fileIO.fileSetLength(0);
        for (HashMap.Entry<K, V> entry : storage.entrySet()) {
            fileIO.writeSize(keySerializer.sizeOfSerialization(entry.getKey()));
            fileIO.writeField(keySerializer.serialize(entry.getKey()));
            fileIO.writeSize(valueSerializer.sizeOfSerialization(entry.getValue()));
            fileIO.writeField(valueSerializer.serialize(entry.getValue()));
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed!");
        }
    }
}



