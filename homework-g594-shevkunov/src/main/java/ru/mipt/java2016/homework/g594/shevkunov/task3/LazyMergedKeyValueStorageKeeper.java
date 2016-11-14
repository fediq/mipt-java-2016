package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * Created by shevkunov on 14.11.16.
 */
public class LazyMergedKeyValueStorageKeeper<K, V> {
    private final Vector<RandomAccessFile> dataFiles = new Vector<>();
    private final LazyMergedKeyValueStorageSerializator<V> valueSerializator;

    public LazyMergedKeyValueStorageKeeper(LazyMergedKeyValueStorageSerializator<K> keySerializator, // TODO Is needed?
                                           LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                                           String fileNamePrefix, String fileNameSuffix,
                                           int dataFilesCount, boolean createNewFiles) throws IOException {
        this.valueSerializator = valueSerializator;
        dataFiles.setSize(dataFilesCount);
        for (int i = 0; i < dataFiles.size(); ++i) {
            String fileName = fileNamePrefix + Integer.toString(i) + fileNameSuffix;
            File tryDataFile = new File(fileName);
            if (createNewFiles) {
                if (tryDataFile.exists()) {
                    throw new RuntimeException("Data files already exists");
                } else {
                    tryDataFile.createNewFile();
                }
            } else {
                if (!tryDataFile.exists()) {
                    throw new RuntimeException("Broken data of header files");
                }
            }
            dataFiles.set(i, new RandomAccessFile(tryDataFile, "rw"));

        }
    }

    public LazyMergedKeyValueStorageFileNode write(int fileIndex, V value) throws IOException {
        return new LazyMergedKeyValueStorageFileNode(fileIndex, writeToFile(dataFiles.get(fileIndex), value));
    }

    public V read(LazyMergedKeyValueStorageFileNode pointer) throws IOException {
        return loadFromFile(dataFiles.get((int) pointer.getFile()), pointer.getOffset());
    }

    private long writeToFile(RandomAccessFile out, V value) throws IOException {
        byte[] bytes = valueSerializator.serialize(value);
        byte[] sizeBytes  = valueSerializator.toBytes(bytes.length);
        long endOffset = out.length();
        out.seek(endOffset);
        out.write(sizeBytes);
        out.write(bytes);
        return endOffset;
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
