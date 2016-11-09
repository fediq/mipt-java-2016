package ru.mipt.java2016.homework.g594.pyrkin.task2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * FileWorker
 * Created by randan on 10/30/16.
 */
public class FileWorker {

    private final File file;

    private RandomAccessFile randomAccessFile;

    public FileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        file = new File(directoryPath, fileName);
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "r");
    }

    public ByteBuffer read(int bytesToRead) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(bytesToRead);

        while (bytesToRead != 0) {
            --bytesToRead;
            resultBuffer.put(randomAccessFile.readByte());
        }

        resultBuffer.rewind();
        return resultBuffer;
    }

    public int read() throws IOException {
        return randomAccessFile.readInt();
    }

    public void write(ByteBuffer buffer) throws IOException {
        randomAccessFile.write(buffer.array());
    }

    public void write(int size) throws IOException {
        randomAccessFile.writeInt(size);
    }

    public void clear() throws IOException {
        randomAccessFile.close();
        file.delete();
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
    }
}
