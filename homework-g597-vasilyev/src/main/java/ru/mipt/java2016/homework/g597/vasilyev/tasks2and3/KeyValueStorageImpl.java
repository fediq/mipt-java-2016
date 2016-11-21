package ru.mipt.java2016.homework.g597.vasilyev.tasks2and3;

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

    private Map<K, Long> offsets;
    private RandomAccessFile keys;
    private RandomAccessFile values;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private boolean open;

    public KeyValueStorageImpl(String path,
                               Serializer<K> keySerializer,
                               Serializer<V> valueSerializer) throws IOException, ConcurrentStorageAccessException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        Path keysPath = FileSystems.getDefault().getPath(path, "keys");
        Path valuesPath = FileSystems.getDefault().getPath(path, "values");
        try {
            keys = new RandomAccessFile(keysPath.toString(), "rw");
            values = new RandomAccessFile(valuesPath.toString(), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("RandomAccessFile(path, \"rw\") throwed FileNotFoundException");
        }

        try {
            keys.getChannel().tryLock();
        } catch (OverlappingFileLockException e) {
            throw new ConcurrentStorageAccessException();
        }

        offsets = new HashMap<>();

        if (keys.length() > 0) {
            while (keys.getFilePointer() < keys.length()) {
                K key = keySerializer.read(keys);
                long offset = keys.readLong();
                offsets.put(key, offset);
            }
        }

        open = true;
    }

    @Override
    public V read(K key) {
        checkOpen();
        Long offset = offsets.get(key);
        if (offset == null) {
            return null;
        }

        try {
            return readValue(offset);
        } catch (IOException e) {
            throw new RuntimeException("Этого не может быть, потому что не может быть никогда!", e);
        }
    }

    @Override
    public boolean exists(K key) {
        return offsets.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkOpen();
        try {
            values.seek(values.length());
            offsets.put(key, values.getFilePointer());
            valueSerializer.write(value, values);
        } catch (IOException e) {
            throw new RuntimeException("Этого не может быть, потому что не может быть никогда!", e);
        }
    }

    @Override
    public void delete(K key) {
        offsets.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        return offsets.size();
    }

    @Override
    public void close() throws IOException {
        keys.seek(0);
        keys.setLength(0);

        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            keySerializer.write(entry.getKey(), keys);
            keys.writeLong(entry.getValue());
        }

        keys.close();
    }

    private void checkOpen() {
        if (!open) {
            throw new RuntimeException("Writing to closed KeyValueStorage");
        }
    }

    private V readValue(long offset) throws IOException {
        values.seek(offset);
        return valueSerializer.read(values);
    }
}
