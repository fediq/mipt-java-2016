package ru.mipt.java2016.homework.g595.efimochkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class DoubleSerialization implements BaseSerialization<Double> {

    private static DoubleSerialization instance = new DoubleSerialization();

    public static DoubleSerialization getInstance() {return instance;}

    private DoubleSerialization() { }

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
            throw new IOException("Could not write to file.");
        }
    }

}
