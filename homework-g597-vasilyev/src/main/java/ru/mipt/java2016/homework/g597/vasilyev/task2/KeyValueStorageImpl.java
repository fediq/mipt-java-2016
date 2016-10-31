package ru.mipt.java2016.homework.g597.vasilyev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mizabrik on 30.10.16.
 */
public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> map;
    private RandomAccessFile storage;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private boolean open;

    public KeyValueStorageImpl(String path,
                               Serializer<K> keySerializer,
                               Serializer<V> valueSerializer) throws IOException, ConcurrentStorageAccessException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        Path dbPath = FileSystems.getDefault().getPath(path, "ilovehardcoding.db");
        try {
            storage = new RandomAccessFile(dbPath.toString(), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("RandomAccessFile(path, \"rw\") throwed FileNotFoundException");
        }

        try {
            storage.getChannel().tryLock();
        } catch (OverlappingFileLockException e) {
            throw new ConcurrentStorageAccessException();
        }

        map = new HashMap<>();

        if (storage.length() > 0) {
            int size = storage.readInt();
            for (int i = 0; i < size; ++i) {
                K key = keySerializer.read(storage);
                V value = valueSerializer.read(storage);
                map.put(key, value);
            }
        }

        open = true;
    }

    @Override
    public V read(K key) {
        checkOpen();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkOpen();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    private void checkOpen() {
        if (!open) {
            throw new RuntimeException("Writing to closed KeyValueStorage");
        }
    }

    @Override
    public void close() throws IOException {
        storage.seek(0);
        storage.setLength(0);

        storage.writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keySerializer.write(entry.getKey(), storage);
            valueSerializer.write(entry.getValue(), storage);
        }

        storage.close();
    }
}
