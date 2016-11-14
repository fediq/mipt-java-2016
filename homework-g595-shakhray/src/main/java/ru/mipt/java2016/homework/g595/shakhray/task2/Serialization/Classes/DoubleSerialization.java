package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class DoubleSerialization implements StorageSerialization<Double> {

    /**
     * The class is a singleton
     */
    private static DoubleSerialization serialization = new DoubleSerialization();

    private DoubleSerialization() { }

    public static DoubleSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, Double object) throws IOException {
        try {
            file.writeDouble(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        try {
            return file.readDouble();
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
