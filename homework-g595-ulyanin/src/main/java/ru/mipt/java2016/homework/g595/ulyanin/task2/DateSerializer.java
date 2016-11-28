package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ulyanin on 31.10.16.
 */
public class DateSerializer implements Serializer<Date> {
    private static DateSerializer ourInstance = new DateSerializer();

    public static DateSerializer getInstance() {
        return ourInstance;
    }

    private DateSerializer() { }

    @Override
    public void serialize(Date data, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong(data.getTime());
    }

    @Override
    public Date deserialize(DataInputStream dataInputStream) throws IOException {
        return new Date(dataInputStream.readLong());
    }
}
