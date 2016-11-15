package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.g594.pyrkin.task2.FileWorker;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * Created by randan on 11/15/16.
 */
public class StorageFileWorker {
    private final File file;

    private RandomAccessFile randomAccessFile;

    boolean mode = false; // false -- read, true -- write

    public StorageFileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        file = new File(directoryPath, fileName);
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public int read(long offset) throws  IOException {
        randomAccessFile.seek(offset);
        return randomAccessFile.readInt();
    }

    public ByteBuffer read(long offset, int size) throws IOException {
        randomAccessFile.seek(offset);
        ByteBuffer resultBuffer = ByteBuffer.allocate(size);
        while(size > 0){
            --size;
            resultBuffer.put(randomAccessFile.readByte());
        }
        return resultBuffer;
    }

    public void writeToEnd(int size) throws IOException {
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.writeInt(size);
    }

    public void writeToEnd(ByteBuffer buffer) throws IOException {
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.write(buffer.array());
    }

    public long getLength () throws IOException {
        return randomAccessFile.length();
    }
}
