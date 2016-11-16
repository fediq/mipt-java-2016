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

    private boolean isStreamMode = false;

    public StorageFileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        file = new File(directoryPath, fileName);
        tmpFile = new File(directory, "tmp.db");
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
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
        if (isStreamMode()) {
            outputStream.close();
        } else {
            randomAccessFile.close();
        }
    }

    public void startRecopyMode() throws IOException {
        isStreamMode = true;
        randomAccessFile.close();
        tmpFile.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
        outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
    }

    public void endRecopyMode() throws IOException {
        isStreamMode = false;
        inputStream.close();
        outputStream.close();
        tmpFile.renameTo(file);
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public int recopyRead() throws IOException {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = result * 256 + inputStream.read();
        }
        return result;
    }

    public ByteBuffer recopyRead(int size) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(size);

        while (size != 0) {
            --size;
            resultBuffer.put((byte) inputStream.read());
        }

        resultBuffer.rewind();
        return resultBuffer;
    }

    public void streamWrite(int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(size);
        outputStream.write(buffer.array());
    }

    public void streamWrite(ByteBuffer buffer) throws IOException {
        outputStream.write(buffer.array());
    }

    public void startStreamMode() throws IOException {
        isStreamMode = true;
        randomAccessFile.close();
        outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    public void endStreamMode() throws IOException {
        isStreamMode = false;
        outputStream.close();
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public boolean isStreamMode() throws IOException {
        return isStreamMode;
    }
}
