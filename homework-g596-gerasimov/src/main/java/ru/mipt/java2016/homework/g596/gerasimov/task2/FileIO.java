package ru.mipt.java2016.homework.g596.gerasimov.task2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * Created by geras-artem on 31.10.16.
 */

public class FileIO {
    private final File file;
    private final File directory;
    private RandomAccessFile randomAccessFile;

    public FileIO(String directoryName, String fileName) throws IOException {
        this.directory = new File(directoryName);
        if (!directory.exists()) {
            throw new NotDirectoryException("Directory does not exist!");
        }
        this.file = new File(directoryName, fileName);
        this.file.createNewFile();
        this.randomAccessFile = new RandomAccessFile(this.file, "rw");
    }

    public int readSize() throws IOException {
        return randomAccessFile.readInt();
    }

    public ByteBuffer readField(int size) throws IOException {
        byte[] array = new byte[size];
        randomAccessFile.read(array, 0, size);

        ByteBuffer result = ByteBuffer.wrap(array);

        return result;
    }

    public void writeSize(int size) throws IOException {
        randomAccessFile.writeInt(size);
    }

    public void writeField(ByteBuffer code) throws IOException {
        randomAccessFile.write(code.array());
    }

    public void fileSetLength(long newLength) throws IOException {
        randomAccessFile.setLength(newLength);
    }

    public long fileLength() throws IOException {
        return randomAccessFile.length();
    }

    public long fileGetFilePointer() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    public  void fileClose() throws IOException {
        randomAccessFile.close();
    }
}
