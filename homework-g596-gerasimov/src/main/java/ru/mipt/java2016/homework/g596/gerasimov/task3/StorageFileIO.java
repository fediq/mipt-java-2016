package ru.mipt.java2016.homework.g596.gerasimov.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * Created by geras-artem on 17.11.16.
 */
public class StorageFileIO {
    private File file;

    private File tmpFile;

    private RandomAccessFile randomAccessFile;

    private BufferedInputStream inputStream;

    private BufferedOutputStream outputStream;

    public StorageFileIO(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("Directory not found");
        }
        file = new File(directoryPath, fileName);
        tmpFile = new File(directoryPath, "tmp.db");
        file.createNewFile();
        outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public int readSize(long offset) throws IOException {
        randomAccessFile.seek(offset);
        return randomAccessFile.readInt();
    }

    public ByteBuffer readField(int size) throws IOException {
        ByteBuffer result = ByteBuffer.allocate(size);
        randomAccessFile.readFully(result.array());
        return result;
    }

    public void streamWriteSize(int size) throws IOException {
        ByteBuffer toWrite = ByteBuffer.allocate(4);
        toWrite.putInt(size);
        outputStream.write(toWrite.array());
    }

    public void streamWriteField(ByteBuffer toWrite) throws IOException {
        outputStream.write(toWrite.array());
    }

    public void enterCopyMode() throws IOException {
        randomAccessFile.close();
        outputStream.close();
        tmpFile.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
        outputStream = new BufferedOutputStream((new FileOutputStream(tmpFile)));
    }

    public void exitCopyMode() throws IOException {
        inputStream.close();
        outputStream.close();
        tmpFile.renameTo(file);
        randomAccessFile = new RandomAccessFile(file, "rw");
        outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
    }

    public int copyReadSize() throws IOException {
        byte[] array = new byte[4];
        inputStream.read(array, 0, 4);

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = result * 256 + array[i];
        }
        return result;
    }

    public ByteBuffer copyReadField(int size) throws IOException {
        byte[] array = new byte[size];
        inputStream.read(array, 0, size);
        return ByteBuffer.wrap(array);
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public long fileLength() throws IOException {
        return randomAccessFile.length();
    }

    public void close() throws IOException {
        outputStream.close();
        randomAccessFile.close();
    }
}