package ru.mipt.java2016.homework.g595.efimochkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class IntegerSerialization implements BaseSerialization<Integer> {

    private static IntegerSerialization instance = new IntegerSerialization();

    public static IntegerSerialization getInstance() {return instance;}

    private IntegerSerialization() { }

    @Override
    public void write(RandomAccessFile file, Integer object) throws IOException {
        try {
            file.writeInt(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        try {
            return file.readInt();
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }
}
