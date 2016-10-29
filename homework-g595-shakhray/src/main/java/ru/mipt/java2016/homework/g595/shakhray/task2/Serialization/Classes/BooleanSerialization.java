package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class BooleanSerialization implements StorageSerialization<Boolean> {

    /**
     * The class is a singleton
     */
    private static BooleanSerialization serialization = new BooleanSerialization();

    private BooleanSerialization() { }

    public static BooleanSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        try {
            file.writeBoolean(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        try {
            return file.readBoolean();
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
