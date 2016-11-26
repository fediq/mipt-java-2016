package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class StringSerialization implements StorageSerialization<String> {

    /**
     * Required serialization of type Integer
     */
    private IntegerSerialization integerSerialization = IntegerSerialization.getSerialization();

    /**
     * The class is a singleton
     */
    private static StringSerialization serialization = new StringSerialization();

    private StringSerialization() { }

    public static StringSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        try {
            byte[] byteArray = object.getBytes();
            integerSerialization.write(file, byteArray.length);
            file.write(byteArray);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        try {
            Integer size = integerSerialization.read(file);
            byte[] byteArray = new byte[size];
            file.readFully(byteArray);
            return new String(byteArray);
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
