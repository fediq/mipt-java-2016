package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages head-file
 * Created by shevkunov on 24.10.16.
 */
class LazyMergedKeyValueStorageHeader<K, V> {
    private final LazyMergedKeyValueStorageSerializator<K> keySerializator;
    private final HashMap<K, Long> pointers = new HashMap<>();
    private final File file;
    private long lazyPointers;
    private long dataFileSize;
    private String fileHDR;

    final boolean createdByConstructor; // True if base doesn't exist before constructor call

    LazyMergedKeyValueStorageHeader(LazyMergedKeyValueStorageSerializator<K> keySerializator,
                                    LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                                    String fileName) throws IOException {
        file = new File(fileName);
        this.keySerializator = keySerializator;
        String tryFileHDR = keySerializator.name() + valueSerializator.name();
        createdByConstructor = !file.exists();
        if (createdByConstructor) { // New Storage
            fileHDR = tryFileHDR;
            dataFileSize = 0;
            lazyPointers = 0;
            write();
        }

        InputStream in = new BufferedInputStream(new FileInputStream(file));
        byte[] read = new byte[(int) readLong(in)];
        in.read(read);
        fileHDR = new String(read);

        if (!fileHDR.equals(tryFileHDR)) {
            throw new RuntimeException("Bad file");
        }

        dataFileSize = readLong(in);

        long records = readLong(in);
        lazyPointers = readLong(in);

        for (int i = 0; i < records; ++i) {
            long size = readLong(in);
            byte[] keyBytes = new byte[(int) size];
            in.read(keyBytes);
            pointers.put(this.keySerializator.deSerialize(keyBytes), readLong(in));
        }
        in.close();
    }

    private long readLong(InputStream in) throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes);
        return keySerializator.toLong(bytes);
    }

    Map<K, Long> getMap() {
        return Collections.unmodifiableMap(pointers);
        // why not?
    }

    HashMap<K, Long> getUnsaveMap() {
        return pointers;
    }

    public long getFileSize() {
        return dataFileSize;
    }

    void addKey(K key, long pointer) {
        if (!pointers.containsKey(key)) {
            ++dataFileSize;
        } else {
            ++lazyPointers;
        }
        pointers.put(key, pointer);
    }

    boolean deleteKey(K key) {
        if (pointers.remove(key) != null) {
            --dataFileSize;
            ++lazyPointers;
            return true;
        } else {
            return false;
        }
    }

    long getLazyPointers() {
        return lazyPointers;
    }

    public void setLazyPointers(long lazyPointers) {
        this.lazyPointers = lazyPointers;
    }

    void write() throws IOException {
        file.delete();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bytesFileHDR = fileHDR.getBytes();
        out.write(keySerializator.toBytes(bytesFileHDR.length));
        out.write(bytesFileHDR);

        out.write(keySerializator.toBytes(dataFileSize));
        out.write(keySerializator.toBytes(pointers.size()));


        out.write(keySerializator.toBytes(lazyPointers));
        for (Map.Entry<K, Long> entry : pointers.entrySet()) {
            byte[] bytes = keySerializator.serialize(entry.getKey());
            byte[] offsetBytes = keySerializator.toBytes(entry.getValue());

            out.write(keySerializator.toBytes(bytes.length));
            out.write(bytes);
            out.write(offsetBytes);
        }

        out.close();
    }
}
