package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationDate extends Serialization<Date> {

    @Override
    public void write(RandomAccessFile file, Date obj) throws IOException {
        file.writeLong(obj.getTime());
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }
}
