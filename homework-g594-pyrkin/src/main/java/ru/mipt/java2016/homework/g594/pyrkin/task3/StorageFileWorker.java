package ru.mipt.java2016.homework.g594.pyrkin.task3;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * Created by randan on 11/15/16.
 */

public class StorageFileWorker {
    private File file;

    private final File tmpFile;

    private RandomAccessFile randomAccessFile;

    private BufferedInputStream inputStream;

    private BufferedOutputStream outputStream;

    private ByteBuffer tmp = ByteBuffer.allocate(4);

    public StorageFileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        file = new File(directoryPath, fileName);
        tmpFile = new File(directory, "tmp.db");
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
        outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    public int read(long offset) throws IOException {
        randomAccessFile.seek(offset);
        return randomAccessFile.readInt();
    }

    public ByteBuffer read(int size) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(size);
        randomAccessFile.readFully(resultBuffer.array());
        return resultBuffer;
    }

    public long getLength() throws IOException {
        return randomAccessFile.length();
    }

    public void close() throws IOException {
        tmp.putInt(0, -1);
        outputStream.write(tmp.array());
        outputStream.close();
        randomAccessFile.close();
    }

    public void startRecopyMode() throws IOException {
        randomAccessFile.close();
        outputStream.close();
        tmpFile.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
        outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
    }

    public void endRecopyMode() throws IOException {
        inputStream.close();
        outputStream.close();
        tmpFile.renameTo(file);
        randomAccessFile = new RandomAccessFile(file, "rw");
        outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    public int recopyRead() throws IOException {
        inputStream.read(tmp.array(), 0, 4);
        return tmp.getInt(0);
    }

    public ByteBuffer recopyRead(int size) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(size);

        inputStream.read(resultBuffer.array(), 0, size);

        return resultBuffer;
    }

    public void streamWrite(int size) throws IOException {
        tmp.putInt(0, size);
        outputStream.write(tmp.array());
    }

    public void streamWrite(ByteBuffer buffer) throws IOException {
        outputStream.write(buffer.array());
    }

    public void flush() throws IOException {
        outputStream.flush();
    }
}
