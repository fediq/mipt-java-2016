package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationDate implements MySerialization<Date> {
    static SerializationDate serialize = new SerializationDate();
    private SerializationDate() { };
    public static SerializationDate getSerialization() {
        return serialize;
    }
    @Override
    public void write(RandomAccessFile file, Date value) throws IOException {
        try {
            long time = value.getTime();
            file.writeLong(time);
        } catch (IOException e) {
            throw new IOException("File write error");
        }
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        Date value = null;
        try {
            Long time = file.readLong();
            value = new Date(time);
        } catch (IOException e) {
            throw new IOException("File read error");
        }
        return value;
    }
}
