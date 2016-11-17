package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Manages head-file
 * Created by shevkunov on 24.10.16.
 */
class LazyMergedKeyValueStorageHeader<K, V> {
    private final LazyMergedKeyValueStorageSerializator<K> keySerializator;
    private final HashMap<K, LazyMergedKeyValueStorageFileNode> pointers = new HashMap<>();
    private final Vector<Long> dataFileSizes = new Vector<>();
    private final File file;
    private long lazyPointers;
    private int dataFilesCount;
    private String fileHDR;

    private byte[] bytesChacheForLong = new byte[8];

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
            dataFilesCount = 1;
            dataFileSizes.add((long) 0);
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

        dataFilesCount = (int) readLong(in);
        dataFileSizes.setSize(dataFilesCount);
        for (int i = 0; i < dataFileSizes.size(); ++i) {
            dataFileSizes.set(i, readLong(in));
        }

        long records = readLong(in);
        lazyPointers = readLong(in);

        for (int i = 0; i < records; ++i) {
            long size = readLong(in);
            byte[] keyBytes = new byte[(int) size];
            in.read(keyBytes);
            pointers.put(this.keySerializator.deSerialize(keyBytes), readFileNode(in));
        }
    }

    private long readLong(InputStream in) throws IOException {
        in.read(bytesChacheForLong);
        return keySerializator.toLong(bytesChacheForLong);
    }

    private LazyMergedKeyValueStorageFileNode readFileNode(InputStream in) throws IOException {
        int fileIndex = (int) readLong(in);
        long offset = readLong(in);
        return new LazyMergedKeyValueStorageFileNode(fileIndex, offset);
    }

    Map<K, LazyMergedKeyValueStorageFileNode> getMap() {
        return Collections.unmodifiableMap(pointers);
        // why not?
    }

    HashMap<K, LazyMergedKeyValueStorageFileNode> getUnsaveMap() {
        return pointers;
    }

    long getSize(int fileIndex) {
        return dataFileSizes.get(fileIndex);
    }

    void addKey(K key, LazyMergedKeyValueStorageFileNode pointer) {
        if (!pointers.containsKey(key)) {
            dataFileSizes.set(pointer.getFile(), dataFileSizes.get(pointer.getFile()) + 1);
        } else {
            ++lazyPointers;
        }
        pointers.put(key, pointer);
    }

    void deleteKey(K key) {
        LazyMergedKeyValueStorageFileNode pointer = pointers.get(key);
        pointers.remove(key);
        dataFileSizes.set(pointer.getFile(), dataFileSizes.get(pointer.getFile()) - 1);
        ++lazyPointers;
    }

    int getDataFilesCount() {
        return dataFilesCount;
    }

    long getLazyPointers() {
        return lazyPointers;
    }

    void write() throws IOException {
        file.delete();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bytesFileHDR = fileHDR.getBytes();
        out.write(keySerializator.toBytes(bytesFileHDR.length));
        out.write(bytesFileHDR);

        out.write(keySerializator.toBytes(dataFilesCount));
        for (int i = 0; i < dataFileSizes.size(); ++i) {
            out.write(keySerializator.toBytes(dataFileSizes.get(i)));
        }
        out.write(keySerializator.toBytes(pointers.size()));

        out.write(keySerializator.toBytes(lazyPointers));
        for (Map.Entry<K, LazyMergedKeyValueStorageFileNode> entry : pointers.entrySet()) {
            byte[] bytes = keySerializator.serialize(entry.getKey());
            byte[] fileBytes = keySerializator.toBytes(entry.getValue().getFile());
            byte[] offsetBytes = keySerializator.toBytes(entry.getValue().getOffset());

            out.write(keySerializator.toBytes(bytes.length));
            out.write(bytes);
            out.write(fileBytes);
            out.write(offsetBytes);
        }
        out.close();
    }
}
