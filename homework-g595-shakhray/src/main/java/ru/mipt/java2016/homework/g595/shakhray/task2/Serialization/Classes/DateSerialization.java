package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Vlad on 29/10/2016.
 */
public class DateSerialization implements StorageSerialization<Date> {

    private LongSerialization longSerialization = LongSerialization.getSerialization();

    /**
     * The class is a singleton
     */
    private static DateSerialization serialization = new DateSerialization();

    private DateSerialization() { }

    public static DateSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        try {
            Long time = object.getTime();
            longSerialization.write(file, time);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        try {
            Long time = longSerialization.read(file);
            return new Date(time);
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
