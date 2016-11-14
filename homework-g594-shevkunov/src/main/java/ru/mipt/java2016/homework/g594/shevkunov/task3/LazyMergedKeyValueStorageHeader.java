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
    private long dataFilesCount; /// TODO REWRITE IN INTS
    private long lazyPointers;
    private String fileHDR;

    public final boolean createdByConstructor; // True if base doesn't exist before constructor call

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

        FileInputStream in = new FileInputStream(file);
        byte[] read = new byte[(int) readLong(in)];
        in.read(read);
        fileHDR = new String(read);

        if (!fileHDR.equals(tryFileHDR)) {
            throw new RuntimeException("Bad file");
        }

        dataFilesCount = readLong(in);
        dataFileSizes.setSize((int) dataFilesCount);
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

    private long readLong(FileInputStream in) throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes);
        return keySerializator.toLong(bytes);
    }

    private LazyMergedKeyValueStorageFileNode readFileNode(FileInputStream in) throws IOException {
        long fileIndex = readLong(in);
        long offset = readLong(in);
        return new LazyMergedKeyValueStorageFileNode(fileIndex, offset);
    }

    public Map<K, LazyMergedKeyValueStorageFileNode> getMap() {
        return Collections.unmodifiableMap(pointers);
        // why not?
    }

    public long getSize(int fileIndex) {
        return dataFileSizes.get(fileIndex);
    }

    public void addKey(K key, LazyMergedKeyValueStorageFileNode pointer) {
        pointers.put(key, pointer);
    }

    public void deleteKey(K key) {
        LazyMergedKeyValueStorageFileNode pointer = pointers.get(key);
        pointers.remove(key);
        dataFileSizes.set((int) pointer.getFile(), dataFileSizes.get((int) pointer.getFile()) - 1);
        lazyPointers++;
    }

    public long getDataFilesCount() {
        return dataFilesCount;
    }

    public void write() throws IOException {
        file.delete();
        FileOutputStream out = new FileOutputStream(file);
        byte[] bytesFileHDR = fileHDR.getBytes();
        out.write(keySerializator.toBytes((Integer) bytesFileHDR.length)); // TODO WHAT A FUCK
        out.write(bytesFileHDR);

        out.write(keySerializator.toBytes((int) dataFilesCount));
        for (int i = 0; i < dataFileSizes.size(); ++i) {
            out.write(keySerializator.toBytes((int) ((long) dataFileSizes.get(i))));
        }
        out.write(keySerializator.toBytes((Integer) pointers.size()));

        out.write(keySerializator.toBytes((int) lazyPointers));
        for (Map.Entry<K, LazyMergedKeyValueStorageFileNode> entry : pointers.entrySet()) {
            byte[] bytes = keySerializator.serialize(entry.getKey());
            byte[] fileBytes = keySerializator.toBytes(entry.getValue().getFile());
            byte[] offsetBytes = keySerializator.toBytes(entry.getValue().getOffset());

            out.write(keySerializator.toBytes((Integer) bytes.length));
            out.write(bytes);
            out.write(fileBytes);
            out.write(offsetBytes);
        }
    }
}
