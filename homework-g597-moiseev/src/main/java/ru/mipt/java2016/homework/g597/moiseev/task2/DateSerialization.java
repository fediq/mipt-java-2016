package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Стратегия сериализации для Double
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */
public class DateSerialization implements Serialization<Date> {
    private static DateSerialization ourInstance = new DateSerialization();

    public static DateSerialization getInstance() {
        return ourInstance;
    }

    private DateSerialization() {
    }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }
}
