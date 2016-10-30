package ru.mipt.java2016.homework.g594.pyrkin.task2;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.NotDirectoryException;
import java.util.Vector;

/**
 * FileWorker
 * Created by randan on 10/30/16.
 */
public class FileWorker {

    private final File directory;
    private final File file;

    public FileWorker(String directoryPath, String fileName) throws IOException {
        this.directory = new File(directoryPath);
        if(!this.directory.exists())
            throw new NotDirectoryException("directory not found");
        this.file = new File(directoryPath, fileName);
        this.file.createNewFile();
    }

    public ByteBuffer read (int bytesToRead) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(bytesToRead);
        RandomAccessFile file = new RandomAccessFile(this.file, "r");

        while(bytesToRead != 0){
            --bytesToRead;
            resultBuffer.put(file.readByte());
        }
        file.close();
        return resultBuffer;
    }

    public int read () throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.file, "r");
        return file.readInt();
    }

    public void write (ByteBuffer buffer) throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.file, "w");
        while(true) {
            try {
                file.write(buffer.get());
            }catch (BufferUnderflowException e){
                break;
            }
        }
    }

    public void write (int size) throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.file, "w");
        file.write(size);
    }
}
