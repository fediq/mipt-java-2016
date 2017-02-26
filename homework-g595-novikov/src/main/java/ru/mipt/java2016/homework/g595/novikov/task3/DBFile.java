package ru.mipt.java2016.homework.g595.novikov.task3;

import ru.mipt.java2016.homework.g595.novikov.task2.MySerialization;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 10/31/16.
 */
public class DBFile<K, V> implements Closeable {
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private MySerialization<Long> longSerialization = new LongSerialization();
    private Map<K, Long> keyOffsets = new HashMap<>();
    private int index;

    private File keysFile;
    private File valuesFile;
    private RandomAccessFile randomAccessFile;

    DBFile(Map<K, V> values, String path, int myIndex, MySerialization<K> myKeySerialization,
           MySerialization<V> myValueSerialization) throws IOException {
        // ths constructor creates new file
        keySerialization = myKeySerialization;
        valueSerialization = myValueSerialization;
        index = myIndex;

        String valuesFilename = String.format("%s%svalues_%d.db", path, File.separator, index);
        String keysFilename = String.format("%s%skeys_%d.db", path, File.separator, index);
        valuesFile = new File(valuesFilename);
        keysFile = new File(keysFilename);
        if (!valuesFile.createNewFile() || !keysFile.createNewFile()) { // need to remove unused files if error occurred
            throw new RuntimeException("Cannot create new DBFile : file already exists");
        }

        randomAccessFile = new RandomAccessFile(valuesFile, "rw");
        for (Map.Entry<K, V> iter : values.entrySet()) {
            keyOffsets.put(iter.getKey(), randomAccessFile.getFilePointer());
            valueSerialization.serialize(randomAccessFile, iter.getValue());
        }
    }

    DBFile(String path, int myIndex, MySerialization<K> myKeySerialization, MySerialization<V> myValueSerialization)
            throws IOException {
        // this constructor opens existing file
        keySerialization = myKeySerialization;
        valueSerialization = myValueSerialization;
        index = myIndex;

        String valuesFilename = String.format("%s%svalues_%d.db", path, File.separator, index);
        String keysFilename = String.format("%s%skeys_%d.db", path, File.separator, index);

        valuesFile = new File(valuesFilename);
        keysFile = new File(keysFilename);
        if (!valuesFile.exists() || !keysFile.exists()) {
            throw new IllegalStateException("Cannot create DBFile from non-existing files");
        }

        randomAccessFile = new RandomAccessFile(valuesFile, "rw");
        DataInputStream keysInput = new DataInputStream(new FileInputStream(keysFile));

        long size = longSerialization.deserialize(keysInput);
        for (long q = 0; q < size; ++q) {
            K key = keySerialization.deserialize(keysInput);
            Long offset = longSerialization.deserialize(keysInput);
            keyOffsets.put(key, offset);
        }
    }

    public V read(K key) throws IOException {
        Long offset = keyOffsets.get(key);
        if (offset == null) {
            return null;
        }
        randomAccessFile.seek(offset);
        return valueSerialization.deserialize(randomAccessFile);
    }

    public void remove(K key) {
        keyOffsets.remove(key);
    }

    public int getIndex() {
        return index;
    }

    public void updateIndex(int ind, Map<K, Integer> keysIndex) {
        for (K key : keyOffsets.keySet()) {
            keysIndex.put(key, ind);
        }
    }

    public void flush() throws IOException {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(keysFile))) {
            longSerialization.serialize(output, (long) keyOffsets.size());
            for (Map.Entry<K, Long> entry : keyOffsets.entrySet()) {
                keySerialization.serialize(output, entry.getKey());
                longSerialization.serialize(output, entry.getValue());
            }
            output.flush();
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        randomAccessFile.close();
    }
}
