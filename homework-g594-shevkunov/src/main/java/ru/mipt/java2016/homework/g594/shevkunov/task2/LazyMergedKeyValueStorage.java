package ru.mipt.java2016.homework.g594.shevkunov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of KeyValueStorage based on merging files
 * Created by shevkunov on 22.10.16.
 */
class LazyMergedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String HEADER_NAME = "/storage.db";
    private static final String DATA_NAME = "/storage_0.db";
    private boolean open = true;

    private final String path;
    private final LazyMergedKeyValueStorageHeader header;
    private final LazyMergedKeyValueStorageSerializator<V> valueSerializator;

    private final HashMap<K, V> chache = new HashMap<>();

    LazyMergedKeyValueStorage(LazyMergedKeyValueStorageSerializator<K> keySerializator,
                              LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                              String path) throws Exception {
        this.path = path;
        this.valueSerializator = valueSerializator;
        File dir = new File(path);
        boolean dirOk = dir.exists() && dir.isDirectory();
        if (!dirOk) {
            throw new FileNotFoundException("No such directory");
        }
        try {
            header = new LazyMergedKeyValueStorageHeader(keySerializator,
                    valueSerializator, path + HEADER_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Problems with header-file");
        }

        loadChache();
    }

    @Override
    public V read(K key) {
        checkClosed();
        return chache.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        return chache.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        chache.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkClosed();
        chache.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return chache.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return chache.size();
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        writeChache();
        open = false;
    }

    private void checkClosed() {
        if (!open) {
            throw new RuntimeException("Storage already closed.");
        }
    }

    private void loadChache() throws IOException {
        File data = new File(path + DATA_NAME);
        if (!data.exists()) {
            data.createNewFile();
        }
        RandomAccessFile in = new RandomAccessFile(data, "r");
        HashMap<K, Long> offsets = header.getMap();
        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            chache.put(entry.getKey(), loadFromFile(in, entry.getValue()));
        }
    }

    private void writeChache() throws IOException {
        RandomAccessFile out = new RandomAccessFile(path + DATA_NAME, "rw"); // there is no "w"
        header.getMap().clear();
        for (Map.Entry<K, V> entry : chache.entrySet()) {
            long offset = writeToFile(out, entry.getValue());
            header.getMap().put(entry.getKey(), offset);
        }

        header.write();
    }

    private long writeToFile(RandomAccessFile out, V value) throws IOException {
        byte[] bytes = valueSerializator.serialize(value);
        byte[] sizeBytes  = valueSerializator.toBytes(bytes.length);

        long retOffset = out.length();
        out.seek(retOffset);
        out.write(sizeBytes);
        out.write(bytes);
        return retOffset;
    }

    private V loadFromFile(RandomAccessFile in, long seek) throws IOException {
        byte[] sizeBytes = new byte[8];
        in.seek(seek);
        in.read(sizeBytes);
        long size = valueSerializator.toLong(sizeBytes);
        byte[] bytes = new byte[(int) size];
        in.read(bytes);
        return valueSerializator.deSerialize(bytes);
    }
}
