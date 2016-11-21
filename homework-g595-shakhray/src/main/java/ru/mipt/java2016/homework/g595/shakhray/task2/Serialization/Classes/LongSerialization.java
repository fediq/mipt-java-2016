package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public class LongSerialization implements StorageSerialization<Long> {

    /**
     * The class is a singleton
     */
    private static LongSerialization serialization = new LongSerialization();

    private LongSerialization() { }

    public static LongSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, Long object) throws IOException {
        try {
            file.writeLong(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Long read(RandomAccessFile file) throws IOException {
        try {
            return file.readLong();
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
