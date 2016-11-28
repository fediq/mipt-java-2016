package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * This class works with base-files.
 * Created by shevkunov on 14.11.16.
 */
class LazyMergedKeyValueStorageKeeper<V> {
    private final String fileNamePrefix;
    private final String fileNameSuffix;
    private final Vector<RandomAccessFile> dataFiles = new Vector<>();
    private final LazyMergedKeyValueStorageSerializator<V> valueSerializator;

    LazyMergedKeyValueStorageKeeper(LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                                           String fileNamePrefix, String fileNameSuffix,
                                           boolean createNewFiles) throws IOException {
        this.fileNamePrefix = fileNamePrefix;
        this.fileNameSuffix = fileNameSuffix;
        this.valueSerializator = valueSerializator;
        grow(1, createNewFiles);
    }

    private void grow(int newDataFilesCount, boolean createNewFiles) throws IOException {
        int oldSize = dataFiles.size();
        if (newDataFilesCount < 0) {
            throw new RuntimeException("Can only grow!");
        }
        dataFiles.setSize(oldSize + newDataFilesCount);
        for (int i = oldSize; i < dataFiles.size(); ++i) {
            File tryDataFile = new File(getName(i));
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

    private String getName(int index) {
        return fileNamePrefix + Integer.toString(index) + fileNameSuffix;
    }

    long write(int fileIndex, V value) throws IOException {
        return writeToFile(dataFiles.get(fileIndex), value);
    }

    V read(int fileIndex, long offset) throws IOException {
        return loadFromFile(dataFiles.get(fileIndex), offset);
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

    int newFile() throws IOException {
        grow(1, true);
        return dataFiles.size() - 1;
    }

    void swap(int a, int b) throws IOException {
        dataFiles.get(a).close();
        dataFiles.get(b).close();

        File aFile = new File(getName(a));
        File bFile = new File(getName(b));
        File temp = new File(getName(-1));

        aFile.renameTo(temp);
        bFile.renameTo(aFile);
        temp.renameTo(bFile);

        dataFiles.set(a, new RandomAccessFile(aFile, "rw"));
        dataFiles.set(b, new RandomAccessFile(bFile, "rw"));
    }

    void popBack() throws IOException {
        if (dataFiles.isEmpty()) {
            throw new RuntimeException("Trying to delete unknown file");
        }

        dataFiles.lastElement().close();
        File del = new File(getName(dataFiles.size() - 1));
        if (!del.delete()) {
            throw new RuntimeException("Can't delete file");
        }
        dataFiles.setSize(dataFiles.size() - 1);
    }

    void close() throws IOException {
        for (RandomAccessFile f : dataFiles) {
            f.close();
        }
    }
}
