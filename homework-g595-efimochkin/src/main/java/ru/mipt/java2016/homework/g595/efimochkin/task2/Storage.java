package ru.mipt.java2016.homework.g595.efimochkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers.BaseSerialization;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers.IntegerSerialization;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sergejefimockin on 17.12.16.
 */
public class Storage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map = new HashMap<>();
    private BaseSerialization<K> keySerializer;
    private BaseSerialization<V> valSerializer;
    private IntegerSerialization intSerializer = IntegerSerialization.getInstance();
    private RandomAccessFile randomAccessFile;
    private boolean isClosed = false;
    private String path;

    public Storage(String nPath, BaseSerialization<K> nKeySerializer,
                    BaseSerialization<V> nValSerializer) throws IOException {
        path = nPath;
        keySerializer = nKeySerializer;
        valSerializer = nValSerializer;

        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new IOException("Directory not found!");
        }

        path += "/storage";
        File file = new File(path);
        if (!file.createNewFile()) {
            randomAccessFile = new RandomAccessFile(file, "rw");
            int size = intSerializer.read(randomAccessFile);
            for (int i = 0; i < size; i++) {
                K key = keySerializer.read(randomAccessFile);
                V val = valSerializer.read(randomAccessFile);
                map.put(key, val);
            }
        } else {
            randomAccessFile = new RandomAccessFile(file, "rw");
        }


    }

    @Override
    public V read(K key) {
        isClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return map.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        isClosed();
        map.remove(key);
    }

    @Override
    public Iterator readKeys() {
        isClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        isClosed();
        randomAccessFile.setLength(0);
        intSerializer.write(randomAccessFile, map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keySerializer.write(randomAccessFile, entry.getKey());
            valSerializer.write(randomAccessFile, entry.getValue());
        }
        randomAccessFile.close();
        isClosed = true;
    }

    private void isClosed() {
        if (isClosed) {
            throw new RuntimeException("File already closed!");
        }
    }
}
