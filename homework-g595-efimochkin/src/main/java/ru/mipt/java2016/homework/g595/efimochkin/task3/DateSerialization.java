package ru.mipt.java2016.homework.g595.efimochkin.task3;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class DateSerialization implements BaseSerialization<Date> {

    private static DateSerialization instance = new DateSerialization();

    public static DateSerialization getInstance() {return instance;}

    private DateSerialization() { }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        try {
            file.writeLong(object.getTime());
        }  catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        try {
            return new Date(file.readLong());
        }  catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }    }
