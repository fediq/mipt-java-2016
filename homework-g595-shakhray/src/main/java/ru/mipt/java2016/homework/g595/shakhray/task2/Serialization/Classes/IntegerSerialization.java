package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class IntegerSerialization implements StorageSerialization<Integer> {

    /**
     * The class is a singleton
     */
    private static IntegerSerialization serialization = new IntegerSerialization();

    private IntegerSerialization() { }

    public static IntegerSerialization getSerialization() {
        return serialization;
    }

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
            throw new IOException("Could not read from file.");
        }
    }
}
