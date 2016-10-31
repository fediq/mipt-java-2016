package ru.mipt.java2016.homework.g594.gorelick.task2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;
import java.text.ParseException;


/**
 * Created by alex on 10/31/16.
 */
public class RandomAccessFileManager {
    public RandomAccessFileManager(String directory_name, String file_name) throws IOException {
        directory = new File(directory_name);
        if (!directory.exists()) {
            throw new IOException("Directory doesn't exist");
        }
        file = new File(directory_name, file_name);
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public ByteBuffer read() throws  IOException{
        Integer size = randomAccessFile.readInt();
        ByteBuffer result = ByteBuffer.allocate(size);
        for(int i = 0; i < size; i++)
            result.put(randomAccessFile.readByte());
        result.rewind();
        return result;
    }

    public void newRAF() throws IOException {
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public void closeRAF() throws IOException {
        randomAccessFile.close();
    }

    public void clearRAF() throws IOException {
        file.delete();
        file.createNewFile();
    }

    public void write(ByteBuffer object) throws IOException {
        randomAccessFile.writeInt(object.capacity());
        randomAccessFile.write(object.array());
    }
    private RandomAccessFile randomAccessFile;
    private File directory;
    private File file;
}
