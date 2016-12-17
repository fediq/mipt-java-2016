package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;


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
    public Date read(RandomAccessFile fileName) throws IOException {
        long date = fileName.readLong();
        return new Date(date);
    }

    @Override
    public Long write(RandomAccessFile fileName, Date data) throws IOException {
        Long offset = fileName.getFilePointer();
        fileName.writeLong(data.getTime());
        return offset;
    }
}
