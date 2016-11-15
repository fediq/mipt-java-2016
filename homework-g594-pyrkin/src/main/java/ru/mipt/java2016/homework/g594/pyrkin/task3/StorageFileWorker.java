package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.g594.pyrkin.task2.FileWorker;

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

    boolean mode = false; // false -- read, true -- write

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

    public void close() throws IOException {
        randomAccessFile.close();
    }

    public void startRecopyMode () throws IOException {
        randomAccessFile.close();
        tmpFile.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void endRecopyMode () throws IOException {
        inputStream.close();
        outputStream.close();
        File oldFile = file;
        file = tmpFile;
        file.renameTo(oldFile);
        oldFile.delete();
    }

    public int recopyRead () throws IOException {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = result * 8 + inputStream.read();
        }
        return result;
    }

    public ByteBuffer recopyRead (int size) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(size);

        while (size != 0) {
            --size;
            resultBuffer.put((byte) inputStream.read());
        }

        resultBuffer.rewind();
        return resultBuffer;
    }

    public void recopyWrite (int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(size);
        outputStream.write(buffer.array());
    }

    public void recopyWrite (ByteBuffer buffer) throws IOException {
        outputStream.write(buffer.array());
    }
}
