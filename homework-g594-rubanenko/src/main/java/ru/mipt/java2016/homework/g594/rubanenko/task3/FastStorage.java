package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by king on 18.11.16.
 */

public class FastStorage {
    private BufferedInputStream input;
    private BufferedOutputStream output;
    private File storageInstance;
    private File bufferStorageInstance;
    private RandomAccessFile randomAccessFile;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);

    public FastStorage(String fileDirectory, String fileName) throws IOException {
        File directory = new File(fileDirectory);
        if (directory.exists()) {
            storageInstance = new File(fileDirectory, fileName);
            bufferStorageInstance = new File(fileDirectory, "buffer.db");
            storageInstance.createNewFile();
            randomAccessFile = new RandomAccessFile(storageInstance, "rw");
            /* ! 'true' because we rather write to the end of the file */
            output = new BufferedOutputStream(new FileOutputStream(storageInstance, true));
        } else {
            throw new IllegalStateException("There is no such directory");
        }
    }

    public void writeSizeToStream(int size) throws IOException {
        byteBuffer.putInt(0, size);
        output.write(byteBuffer.array());
    }

    public void writeBytesToStream(ByteBuffer bytesToWrite) throws IOException {
        output.write(bytesToWrite.array());
    }

    public void flushStream() throws IOException {
        output.flush();
    }

    public int readKey(long offset) throws IOException {
        randomAccessFile.seek(offset);
        return randomAccessFile.readInt();
    }

    public ByteBuffer readBytes(int size) throws IOException {
        ByteBuffer value = ByteBuffer.allocate(size);
        randomAccessFile.readFully(value.array());
        return value;
    }

    public int readKeyFromStream() throws IOException {
        if (input.read(byteBuffer.array(), 0, Integer.SIZE / 8) != Integer.SIZE / 8) {
            throw new IOException("Error during reading");
        }
        return byteBuffer.getInt(0);
    }

    public ByteBuffer readValueFromStream(int size) throws IOException {
        ByteBuffer value = ByteBuffer.allocate(size);
        if (input.read(value.array(), 0, size) != size) {
            throw new IOException("Error during reading");
        }
        return value;
    }

    public long getLength() throws IOException {
        return randomAccessFile.length();
    }

    public void close() throws IOException {
        byteBuffer.putInt(0, Integer.MIN_VALUE);
        output.write(byteBuffer.array());
        output.close();
        randomAccessFile.close();
    }

    public void beginUpdate() throws IOException {
        output.close();
        randomAccessFile.close();
        bufferStorageInstance.createNewFile();
        input = new BufferedInputStream(new FileInputStream(storageInstance));
        output = new BufferedOutputStream(new FileOutputStream(bufferStorageInstance));
    }

    public void endUpdate() throws IOException {
        output.close();
        input.close();
        bufferStorageInstance.renameTo(storageInstance);
        randomAccessFile = new RandomAccessFile(storageInstance, "rw");
        output = new BufferedOutputStream(new FileOutputStream(storageInstance, true));
    }
}
