package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 31.10.16.
 */
public class MySerializationDate implements MySerialization<Date> {
    private static final MySerializationDate SERIALIZATION = new MySerializationDate();

    public static MySerializationDate getSerialization() {
        return SERIALIZATION;
    }

    @Override
    public Date read(DataInputStream file) throws IOException {
        return new Date(file.readLong());
    }

    @Override
    public void write(DataOutputStream file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }
}
