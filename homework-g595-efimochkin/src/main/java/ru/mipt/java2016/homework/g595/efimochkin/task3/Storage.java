package ru.mipt.java2016.homework.g595.efimochkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by sergejefimockin on 28.11.16.
 */
public class Storage<K,V> implements KeyValueStorage <K, V> {

    private BaseSerialization<K> keySerialization;
    private BaseSerialization<V> valueSerialization;
    private BaseSerialization <Long> offsetSerializationStrategy = LongSerialization.getInstance();
    private HashMap<K, V> buffer = new HashMap<>();
    private HashMap<K, Long> offsets = new HashMap<>();
    private boolean Closed;
    private RandomAccessFile storage;
    private int deleteCount;
    private K cacheKey;
    private V cacheValue;
    private boolean cacheUsed = false;
    private String path;
    private RandomAccessFile keyOffsetTable;


    private final String storageName = "Storage";
    private final String mapName = "StMap";

    public Storage(String pathArg, BaseSerialization<K> keySerializationArg,
                   BaseSerialization<V> valueSerializationArg) {
        keySerialization = keySerializationArg;
        valueSerialization = valueSerializationArg;
        path = pathArg;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += File.separator +  "file";
        }

        File storageFile = new File(path + storageName);
        File mapFile = new File(path + mapName);
        try {
            storage = new RandomAccessFile(storageFile, "rw");
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file");
        }
        if (storageFile.exists() && mapFile.exists()) {
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = mapTempFile.readInt();
                deleteCount = mapTempFile.readInt();
                for (int i = 0; i < n; i++) {
                    K key = keySerialization.read(mapTempFile);
                    Long offset = offsetSerializationStrategy.read(mapTempFile);
                    offsets.put(key, offset);
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not read from file");
            }
        }
    }




    @Override
    public V read(K key) {
        checkState();
        if (cacheUsed && cacheKey == key) {
            return cacheValue;
        }
        if (!offsets.containsKey(key)) {
            return null;
        }
        long offset = offsets.get(key);

        if (offset < 0) {
            V result = buffer.get(key);
            cacheUsed = true;
            cacheKey = key;
            cacheValue = result;
            return buffer.get(key);
        }
        try {
            V result = valueSerialization.read(storage);
            storage.seek(offset);
            cacheUsed = true;
            cacheKey = key;
            cacheValue = result;
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file");
        }
    }

    @Override
    public boolean exists(K key) {
        checkState();
        return offsets.containsKey(key);
    }


    private void checkState() {
        if (Closed) {
            throw new RuntimeException("Closed File");
        }
    }

    @Override
    public void write(K key, V value) {
        checkState();
        offsets.put(key, (long) -1);
        buffer.put(key, value);
        if (buffer.size() >= 1000) {
            try {
                dump();
            } catch (IOException e) {
                throw new RuntimeException("Closed File");
            }
        }

    }

    @Override
    public void delete(K key) {

        checkState();
        if (exists(key)) {
            deleteCount++;
            buffer.remove(key);
            offsets.remove(key);
        }
        if (deleteCount >= 10000) {
            try {
                rewriteFile();
                deleteCount = 0;
            } catch (IOException e) {
                throw new RuntimeException("Closed File");
            }
        }

    }

    @Override
    public Iterator<K> readKeys() {
        checkState();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkState();
        return offsets.size();
    }

    private void dump() throws IOException {
        long offset = storage.length();
        storage.seek(offset);
        keyOffsetTable.seek(keyOffsetTable.length());
        for (Map.Entry<K, V> entry : buffer.entrySet()) {
            keySerialization.write(keyOffsetTable, entry.getKey());
            keyOffsetTable.writeLong(offset);
            offsets.remove(entry.getKey());
            offsets.put(entry.getKey(), offset);
            valueSerialization.write(storage, entry.getValue());
            offset = storage.length();
        }
        buffer.clear();
    }

    private void rewriteFile() throws IOException {
        keyOffsetTable.setLength(0);
        keyOffsetTable.seek(0);

        RandomAccessFile bufFile;
        File pathToFile = Paths.get(path, "storageCopy").toFile();
        try {
            pathToFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not create the file");
        }
        try {
            keyOffsetTable = new RandomAccessFile(pathToFile, "rw");
            bufFile = new RandomAccessFile(pathToFile, "rw");
        } catch (IOException e) {
            throw new IOException("File not found");
        }
        bufFile.seek(0);

        long offset = 0;
        V bufValue;

        for (Map.Entry<K, Long> entry : offsets.entrySet()) {
            if (entry.getValue() >= 0) {
                keySerialization.write(keyOffsetTable, entry.getKey());
                keyOffsetTable.writeLong(offset);
                storage.seek(entry.getValue());
                valueSerialization.write(bufFile, valueSerialization.read(storage));
                offset += bufFile.length();
            }
        }

        File oldFile = Paths.get(path, "storage").toFile();
        oldFile.delete();
        File newFile = Paths.get(path, "storageCopy").toFile();
        newFile.renameTo(oldFile);
        storage = bufFile;
    }

    @Override
    public void close() throws IOException {
        checkState();
        if (buffer.size() != 0) {
            dump();
        }
        if (deleteCount != 0) {
            rewriteFile();
        }
        buffer = null;
        offsets = null;
        deleteCount = 0;
        storage.close();
        keyOffsetTable.close();
        Closed = true;
    }
}

