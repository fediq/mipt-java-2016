package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.IntegerSerializer;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.SerializerInterface;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.StringSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by randan on 11/15/16.
 */
public class SSTableKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final HashMap<K, V> memoryStorage = new HashMap<>();

    private final HashMap<K, Long> offsetTable = new HashMap<>();

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private final IndexFileWorker indexFileWorker;

    private final StorageFileWorker storageFileWorker;

    private boolean isClosed = false;

    private final int maxMemoryStorageSize;

    public SSTableKeyValueStorage(String directoryPath,
                                  SerializerInterface<K> keySerializer,
                                  SerializerInterface<V> valueSerializer,
                                  int maxMemoryStorageSize) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.maxMemoryStorageSize = maxMemoryStorageSize;
        indexFileWorker = new IndexFileWorker(directoryPath, "index.db");
        storageFileWorker = new StorageFileWorker(directoryPath, "storage.db");
        readOffsetTable();
    }

    @Override
    public V read(K key) {
        checkClosed();
        V value = memoryStorage.get(key);
        if (value != null) {
            return value;
        }
        if (!offsetTable.containsKey(key)){
            return null;
        }
        try {
            return readValue(key);
        }catch (IOException exception){
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        return offsetTable.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        memoryStorage.put(key, value);
        offsetTable.put(key, (long)-1);
        if(memoryStorage.size() > maxMemoryStorageSize){
            try {
                flushMemoryStorage();
            }catch (IOException exception){
                throw new RuntimeException("not enough memory");
            }

        }
    }

    @Override
    public void delete(K key) {
        checkClosed();
        memoryStorage.remove(key);
        offsetTable.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return offsetTable.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return offsetTable.size();
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        isClosed = true;

        removeDeletedFromFile();
        flushMemoryStorage();
        storageFileWorker.close();

        writeOffsetTable();
        indexFileWorker.close();
    }

    private void checkClosed() {
        if(isClosed)
            throw new RuntimeException("storage closed");
    }

    private K readOffsetTableKey (int size) throws IOException {
        return keySerializer.deserialize(indexFileWorker.read(size));
    }

    private void readOffsetTable () throws IOException {
        while(true) {
            int size = indexFileWorker.read();
            if (size < 0)
                break;

            offsetTable.put(readOffsetTableKey(size), indexFileWorker.readOffset());
        }
    }

    private void writeOffsetTableField (K key, long offset) throws IOException {
        indexFileWorker.write(keySerializer.sizeOfSerialize(key));
        indexFileWorker.write(keySerializer.serialize(key));
        indexFileWorker.writeOffset(offset);
    }

    private void writeOffsetTable () throws IOException {
        indexFileWorker.clear();
        for(Map.Entry<K, Long> entry : offsetTable.entrySet()) {
            writeOffsetTableField(entry.getKey(), entry.getValue());
        }
        offsetTable.clear();
    }

    private V readValue (K key) throws IOException {
        long offset = offsetTable.get(key);
        int size = storageFileWorker.read(offset);
        return valueSerializer.deserialize(storageFileWorker.read(offset + 4, size));
    }

    private void writeField (K key, V value) throws IOException {
        storageFileWorker.writeToEnd(keySerializer.sizeOfSerialize(key));
        storageFileWorker.writeToEnd(keySerializer.serialize(key));

        storageFileWorker.writeToEnd(valueSerializer.sizeOfSerialize(value));
        storageFileWorker.writeToEnd(valueSerializer.serialize(value));
    }

    private void flushMemoryStorage () throws IOException {
        for(Map.Entry<K, V> entry : memoryStorage.entrySet()){
            offsetTable.put(entry.getKey(), storageFileWorker.getLength() + 4 +
                    keySerializer.sizeOfSerialize(entry.getKey()));
            writeField(entry.getKey(), entry.getValue());
        }
        memoryStorage.clear();
    }

    private void removeDeletedFromFile () throws IOException {
        storageFileWorker.startRecopyMode();
        long currentOffset = 0;
        while(true) {
            int keySize = storageFileWorker.recopyRead();
            if(keySize < 0)
                break;
            ByteBuffer key = storageFileWorker.recopyRead(keySize);
            int valueSize = storageFileWorker.recopyRead();
            ByteBuffer value = storageFileWorker.recopyRead(valueSize);
            K deserealizedKey = keySerializer.deserialize(key);
            if(offsetTable.containsKey(deserealizedKey)) {
                storageFileWorker.recopyWrite(keySize);
                storageFileWorker.recopyWrite(key);
                currentOffset += 4 + keySize;
                offsetTable.put(deserealizedKey, currentOffset);
                storageFileWorker.recopyWrite(valueSize);
                storageFileWorker.recopyWrite(value);
                currentOffset += 4 + valueSize;
            }
        }
        storageFileWorker.endRecopyMode();
    }
}
