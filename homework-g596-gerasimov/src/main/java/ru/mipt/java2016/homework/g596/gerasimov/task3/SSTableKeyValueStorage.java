package ru.mipt.java2016.homework.g596.gerasimov.task3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.ISerializer;

/**
 * Created by geras-artem on 16.11.16.
 */
public class SSTableKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final HashMap<K, Long> offsetTable = new HashMap<>();

    private final ISerializer<K> keySerializer;

    private final ISerializer<V> valueSerializer;

    private final IndexFileIO indexFileIO;

    private final StorageFileIO storageFileIO;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private final Lock readLock = readWriteLock.readLock();

    private boolean isClosed = false;

    private boolean offsetTableIsUpdated = false;

    private int deletedCounter = 0;

    private long storageLength;

    private long writtenLength;

    public SSTableKeyValueStorage(String directoryPath, ISerializer<K> keySerializer,
            ISerializer<V> valueSerializer) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        indexFileIO = new IndexFileIO(directoryPath, "index.db");
        storageFileIO = new StorageFileIO(directoryPath, "storage.db");
        storageLength = storageFileIO.fileLength();
        writtenLength = storageLength - 1;
        readOffsetTable();
    }

    @Override
    public V read(K key) {
        writeLock.lock();
        V result;
        try {
            checkClosed();
            if (!exists(key)) {
                result = null;
            }

            result = readValue(key);
        } catch (Exception exception) {
            result = null;
        } finally {
            writeLock.unlock();
        }
        return result;
    }

    @Override
    public boolean exists(K key) {
        readLock.lock();
        boolean result;
        try {
            checkClosed();
            result = offsetTable.containsKey(key);
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkClosed();
            offsetTableIsUpdated = true;
            writeField(key, value);
        } catch (Exception exception) {
            throw new RuntimeException("Error in write");
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkClosed();
            offsetTableIsUpdated = true;
            ++deletedCounter;
            offsetTable.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        Iterator<K> iterator;
        readLock.lock();
        try {
            checkClosed();
            iterator = offsetTable.keySet().iterator();
        } finally {
            readLock.unlock();
        }
        return iterator;
    }

    @Override
    public int size() {
        int result;
        readLock.lock();
        try {
            checkClosed();
            result = offsetTable.size();
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        writeLock.lock();
        try {
            checkClosed();
            isClosed = true;

            refreshStorageFile();
            storageFileIO.close();

            if (offsetTableIsUpdated) {
                writeOffsetTable();
                offsetTableIsUpdated = false;
            }
            indexFileIO.close();
        } finally {
            writeLock.unlock();
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed");
        }
    }

    private void readOffsetTable() throws IOException {
        if (indexFileIO.isEmpty()) {
            return;
        }
        for (int size = indexFileIO.readSize(); size > 0; size = indexFileIO.readSize()) {
            offsetTable.put(keySerializer.deserialize(indexFileIO.readField(size)),
                    indexFileIO.readOffset());
        }
    }

    private void writeOffsetTable() throws IOException {
        indexFileIO.clear();
        for (HashMap.Entry<K, Long> entry : offsetTable.entrySet()) {
            indexFileIO.writeSize(keySerializer.sizeOfSerialization(entry.getKey()));
            indexFileIO.writeField(keySerializer.serialize(entry.getKey()));
            indexFileIO.writeOffset(entry.getValue());
        }
        offsetTable.clear();
    }

    private V readValue(K key) throws IOException {
        long offset = offsetTable.get(key);
        if (offset > writtenLength) {
            storageFileIO.flush();
            writtenLength = storageLength - 1;
        }
        int size = storageFileIO.readSize(offset);
        return valueSerializer.deserialize(storageFileIO.readField(size));
    }

    private void writeField(K key, V value) throws IOException {
        storageLength += 4 + keySerializer.sizeOfSerialization(key);
        storageFileIO.writeSize(keySerializer.sizeOfSerialization(key));
        storageFileIO.writeField(keySerializer.serialize(key));

        offsetTable.put(key, storageLength);

        storageLength += 4 + valueSerializer.sizeOfSerialization(value);
        storageFileIO.writeSize(valueSerializer.sizeOfSerialization(value));
        storageFileIO.writeField(valueSerializer.serialize(value));
    }

    private void refreshStorageFile() throws IOException {
        if (deletedCounter > 3 * offsetTable.size()) {
            storageFileIO.enterCopyMode();
            storageLength = 0;
            long oldFileOffset = 0;
            for (int keySize = storageFileIO.copyReadSize(); keySize > 0;
                     keySize = storageFileIO.copyReadSize()) {

                ByteBuffer keyCode = storageFileIO.copyReadField(keySize);
                int valueSize = storageFileIO.copyReadSize();
                ByteBuffer valueCode = storageFileIO.copyReadField(valueSize);
                K key = keySerializer.deserialize(keyCode);

                if (offsetTable.containsKey(key) && offsetTable.get(key)
                        .equals(oldFileOffset)) {

                    storageFileIO.writeSize(keySize);
                    storageFileIO.writeField(keyCode);
                    storageLength += 4 + keySize;

                    offsetTable.put(key, storageLength);

                    storageFileIO.writeSize(valueSize);
                    storageFileIO.writeField(valueCode);
                    storageLength += 4 + valueSize;
                }

                oldFileOffset += 8 + keySize + valueSize;
            }
            storageFileIO.exitCopyMode();
        }
    }
}
