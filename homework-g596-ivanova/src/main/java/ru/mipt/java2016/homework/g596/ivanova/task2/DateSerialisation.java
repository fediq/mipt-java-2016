package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by julia on 30.10.16.
 */
public final class DateSerialisation implements Serialisation<Date> {
    /**
     * Instance of current class.
     */
    private static DateSerialisation instance = new DateSerialisation();

    /**
     * Constructor for the class.
     */
    private DateSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static DateSerialisation getInstance() {
        return instance;
    }

    @Override
    public Date read(final DataInput file) throws IOException {
        return new Date(file.readLong());
    }

    @Override
    public long write(final DataOutput file, final Date object) throws IOException {
        long longSize = 8;
        file.writeLong(object.getTime());
        return longSize;
    }
}
