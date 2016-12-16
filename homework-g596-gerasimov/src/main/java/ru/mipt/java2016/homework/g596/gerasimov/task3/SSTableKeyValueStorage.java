package ru.mipt.java2016.homework.g596.gerasimov.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
    private static final int INT_SIZE = Integer.SIZE / 8;

    private static final int REFRESH_CONST = 3;

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

    private int oldNoteCounter;

    private long storageLength;

    private long writtenLength;

    private long cacheSize;

    private LoadingCache<K, V> cache;

    public SSTableKeyValueStorage(String directoryPath, ISerializer<K> keySerializer,
            ISerializer<V> valueSerializer, long cacheSize) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        indexFileIO = new IndexFileIO(directoryPath, "index.db");
        storageFileIO = new StorageFileIO(directoryPath, "storage.db");
        storageLength = storageFileIO.fileLength();
        writtenLength = storageLength - 1;
        readOffsetTable();

        this.cacheSize = cacheSize;
        cache = CacheBuilder.newBuilder()
                .maximumSize(this.cacheSize)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        if (offsetTable.containsKey(key)) {
                            try {
                                return readValue(key);
                            } catch (Exception exception) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                });
    }

    @Override
    public V read(K key) {
        readLock.lock();
        try {
            checkClosed();
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            return cache.get(key);
        } catch (Exception exception) {
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        readLock.lock();
        try {
            checkClosed();
            return offsetTable.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        readLock.lock();
        try {
            checkClosed();
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            offsetTableIsUpdated = true;
            if (offsetTable.containsKey(key)) {
                ++oldNoteCounter;
            }
            writeField(key, value);

            if (cache.asMap().containsKey(key)) {
                cache.put(key, value);
            }

            refreshStorageFile();
        } catch (Exception exception) {
            throw new RuntimeException("Error in write");
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        readLock.lock();
        try {
            checkClosed();
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            offsetTableIsUpdated = true;
            ++oldNoteCounter;
            offsetTable.remove(key);
            cache.invalidate(key);
            refreshStorageFile();
        } catch (Exception exception) {
            throw new RuntimeException("Error in delete");
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        readLock.lock();
        try {
            checkClosed();
            return offsetTable.keySet().iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            checkClosed();
            return offsetTable.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        readLock.lock();
        try {
            checkClosed();
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            isClosed = true;
            refreshStorageFile();
            if (offsetTableIsUpdated) {
                writeOffsetTable();
                offsetTableIsUpdated = false;
            }
        } finally {
            try {
                storageFileIO.epicClose();
                indexFileIO.close();
                cache.cleanUp();
            } finally {
                writeLock.unlock();
            }
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
        storageLength += INT_SIZE + keySerializer.sizeOfSerialization(key);
        storageFileIO.writeSize(keySerializer.sizeOfSerialization(key));
        storageFileIO.writeField(keySerializer.serialize(key));

        offsetTable.put(key, storageLength);

        storageLength += INT_SIZE + valueSerializer.sizeOfSerialization(value);
        storageFileIO.writeSize(valueSerializer.sizeOfSerialization(value));
        storageFileIO.writeField(valueSerializer.serialize(value));
    }

    private void refreshStorageFile() throws IOException {
        if (oldNoteCounter <= REFRESH_CONST * offsetTable.size()) {
            return;
        }
        try (StorageFileIO storageFileIO = this.storageFileIO.open()) {
            storageLength = 0;
            long oldFileOffset = 0;
            for (int keySize = storageFileIO.copyReadSize();
                 keySize > 0; keySize = storageFileIO.copyReadSize()) {

                ByteBuffer keyCode = storageFileIO.copyReadField(keySize);
                int valueSize = storageFileIO.copyReadSize();
                ByteBuffer valueCode = storageFileIO.copyReadField(valueSize);
                K key = keySerializer.deserialize(keyCode);

                if (offsetTable.containsKey(key) && offsetTable.get(key).equals(oldFileOffset)) {

                    storageFileIO.writeSize(keySize);
                    storageFileIO.writeField(keyCode);
                    storageLength += INT_SIZE + keySize;

                    offsetTable.put(key, storageLength);

                    storageFileIO.writeSize(valueSize);
                    storageFileIO.writeField(valueCode);
                    storageLength += INT_SIZE + valueSize;
                }

                oldFileOffset += 2 * INT_SIZE + keySize + valueSize;
            }
            oldNoteCounter = 0;
        }
    }
}

