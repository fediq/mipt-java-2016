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

    private int deletedNumber = 0;

    private long currentStorageLength;

    public SSTableKeyValueStorage(String directoryPath,
                                  SerializerInterface<K> keySerializer,
                                  SerializerInterface<V> valueSerializer,
                                  int maxMemoryStorageSize) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.maxMemoryStorageSize = maxMemoryStorageSize;
        indexFileWorker = new IndexFileWorker(directoryPath, "index.db");
        storageFileWorker = new StorageFileWorker(directoryPath, "storage.db");
        currentStorageLength = storageFileWorker.getLength();
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
            if(storageFileWorker.isStreamMode())
                storageFileWorker.endStreamMode();
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
        if(memoryStorage.remove(key) == null)
            ++deletedNumber;
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

        writeStorageToFile();
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

        currentStorageLength += 8 + keySerializer.sizeOfSerialize(key) +
                valueSerializer.sizeOfSerialize(value);
    }

    private void streamWriteField (K key, V value) throws IOException {
        storageFileWorker.streamWrite(keySerializer.sizeOfSerialize(key));
        storageFileWorker.streamWrite(keySerializer.serialize(key));

        storageFileWorker.streamWrite(valueSerializer.sizeOfSerialize(value));
        storageFileWorker.streamWrite(valueSerializer.serialize(value));

        currentStorageLength += 8 + keySerializer.sizeOfSerialize(key) +
                valueSerializer.sizeOfSerialize(value);
    }

    private void writeAllFields () throws IOException {
        for(Map.Entry<K, V> entry : memoryStorage.entrySet()){
            int keySerializeSize = keySerializer.sizeOfSerialize(entry.getKey());
            offsetTable.put(entry.getKey(), currentStorageLength + 4 + keySerializeSize);
            writeField(entry.getKey(), entry.getValue());
        }
    }

    private void streamWriteAllFields () throws IOException {
        for(Map.Entry<K, V> entry : memoryStorage.entrySet()){
            int keySerializeSize = keySerializer.sizeOfSerialize(entry.getKey());
            offsetTable.put(entry.getKey(), currentStorageLength + 4 + keySerializeSize);
            streamWriteField(entry.getKey(), entry.getValue());
        }
    }

    private void flushMemoryStorage () throws IOException {
        if(storageFileWorker.isStreamMode()) {
            streamWriteAllFields();
        } else if(storageFileWorker.getLength() == 0) {
            storageFileWorker.startStreamMode();
            streamWriteAllFields();
        } else {
            writeAllFields();
        }
        memoryStorage.clear();
    }

    private void writeStorageToFile () throws IOException {
        if(deletedNumber > 4 * offsetTable.size() - memoryStorage.size()) {
            if(storageFileWorker.isStreamMode())
                storageFileWorker.endStreamMode();
            storageFileWorker.startRecopyMode();
            currentStorageLength = 0;
            while (true) {
                int keySize = storageFileWorker.recopyRead();
                if (keySize < 0)
                    break;
                ByteBuffer key = storageFileWorker.recopyRead(keySize);
                int valueSize = storageFileWorker.recopyRead();
                ByteBuffer value = storageFileWorker.recopyRead(valueSize);
                K deserealizedKey = keySerializer.deserialize(key);
                if (offsetTable.containsKey(deserealizedKey)) {
                    storageFileWorker.streamWrite(keySize);
                    storageFileWorker.streamWrite(key);
                    currentStorageLength += 4 + keySize;
                    offsetTable.put(deserealizedKey, currentStorageLength);
                    storageFileWorker.streamWrite(valueSize);
                    storageFileWorker.streamWrite(value);
                    currentStorageLength += 4 + valueSize;
                }
            }
            flushMemoryStorage();
            storageFileWorker.endRecopyMode();
        } else if(!memoryStorage.isEmpty()) {
            flushMemoryStorage();
        }

    }
}
