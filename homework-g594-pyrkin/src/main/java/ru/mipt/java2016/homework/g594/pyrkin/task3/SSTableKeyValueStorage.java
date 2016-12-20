package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.SerializerInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by randan on 11/15/16.
 */

public class SSTableKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final HashMap<K, Long> offsetTable = new HashMap<>();

    private final SerializerInterface<K> keySerializer;

    private final SerializerInterface<V> valueSerializer;

    private final IndexFileWorker indexFileWorker;

    private final StorageFileWorker storageFileWorker;

    private boolean isClosed = false;

    private int deletedNumber = 0;

    private long currentStorageLength;

    private boolean offsetTableWasUpdated = false;

    private long writtenLength;


    public SSTableKeyValueStorage(String directoryPath,
                                  SerializerInterface<K> keySerializer,
                                  SerializerInterface<V> valueSerializer) throws IOException {
        synchronized (offsetTable) {
            this.keySerializer = keySerializer;
            this.valueSerializer = valueSerializer;
            indexFileWorker = new IndexFileWorker(directoryPath, "index.db");
            storageFileWorker = new StorageFileWorker(directoryPath, "storage.db");
            currentStorageLength = storageFileWorker.getLength();
            writtenLength = currentStorageLength - 1;
            readOffsetTable();
        }
    }

    @Override
    public V read(K key) {
        synchronized (offsetTable) {
            checkClosed();
            if (!exists(key)) {
                return null;
            }

            try {
                return readValue(key);
            } catch (Exception exception) {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (offsetTable) {
            checkClosed();
            return offsetTable.containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (offsetTable) {
            checkClosed();
            offsetTableWasUpdated = true;
            try {
                writeField(key, value);
            } catch (IOException exception) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void delete(K key) {
        synchronized (offsetTable) {
            offsetTableWasUpdated = true;
            checkClosed();
            ++deletedNumber;
            offsetTable.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (offsetTable) {
            checkClosed();
            return offsetTable.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (offsetTable) {
            checkClosed();
            return offsetTable.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (offsetTable) {
            checkClosed();
            isClosed = true;

            writeStorageToFile();
            storageFileWorker.close();

            if (offsetTableWasUpdated) {
                writeOffsetTable();
                offsetTableWasUpdated = false;
            }
            indexFileWorker.close();
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("storage closed");
        }
    }

    private K readOffsetTableKey(int size) throws IOException {
        return keySerializer.deserialize(indexFileWorker.read(size));
    }

    private void readOffsetTable() throws IOException {
        while (true) {
            int size = indexFileWorker.read();
            if (size <= 0) {
                break;
            }

            offsetTable.put(readOffsetTableKey(size), indexFileWorker.readOffset());
        }
    }

    private void writeOffsetTableField(K key, long offset) throws IOException {
        indexFileWorker.write(keySerializer.sizeOfSerialize(key));
        indexFileWorker.write(keySerializer.serialize(key));
        indexFileWorker.writeOffset(offset);
    }

    private void writeOffsetTable() throws IOException {
        indexFileWorker.clear();
        for (Map.Entry<K, Long> entry : offsetTable.entrySet()) {
            writeOffsetTableField(entry.getKey(), entry.getValue());
        }
        offsetTable.clear();
    }

    private V readValue(K key) throws IOException {
        long offset = offsetTable.get(key);
        if (offset > writtenLength) {
            storageFileWorker.flush();
            writtenLength = currentStorageLength - 1;
        }
        int size = storageFileWorker.read(offset);
        return valueSerializer.deserialize(storageFileWorker.read(size));
    }

    private void writeField(K key, V value) throws IOException {
        int keySerializeSize = keySerializer.sizeOfSerialize(key);
        offsetTable.put(key, currentStorageLength + 4 + keySerializeSize);
        storageFileWorker.streamWrite(keySerializeSize);
        storageFileWorker.streamWrite(keySerializer.serialize(key));

        storageFileWorker.streamWrite(valueSerializer.sizeOfSerialize(value));
        storageFileWorker.streamWrite(valueSerializer.serialize(value));

        currentStorageLength += 8 + keySerializer.sizeOfSerialize(key) +
                valueSerializer.sizeOfSerialize(value);
    }

    private void writeStorageToFile() throws IOException {
        if (deletedNumber > 4 * offsetTable.size()) {
            storageFileWorker.startRecopyMode();
            currentStorageLength = 0;
            while (true) {
                int keySize = storageFileWorker.recopyRead();
                if (keySize <= 0) {
                    break;
                }
                ByteBuffer key = storageFileWorker.recopyRead(keySize);
                int valueSize = storageFileWorker.recopyRead();
                ByteBuffer value = storageFileWorker.recopyRead(valueSize);
                K deserealizedKey = keySerializer.deserialize(key);
                if (offsetTable.containsKey(deserealizedKey) &&
                        offsetTable.get(deserealizedKey) == currentStorageLength + 4 + keySize) {
                    storageFileWorker.streamWrite(keySize);
                    storageFileWorker.streamWrite(key);
                    currentStorageLength += 4 + keySize;
                    offsetTable.put(deserealizedKey, currentStorageLength);
                    storageFileWorker.streamWrite(valueSize);
                    storageFileWorker.streamWrite(value);
                    currentStorageLength += 4 + valueSize;
                }
            }
            storageFileWorker.endRecopyMode();
        }
    }
}
