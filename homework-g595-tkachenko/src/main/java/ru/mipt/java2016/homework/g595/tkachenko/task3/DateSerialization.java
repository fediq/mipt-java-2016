package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;
import java.util.Date;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class DateSerialization extends Serialization<Date> {
    @Override
    public Date read(DataInput input) throws IOException {
        return new Date(input.readLong());
    }

    @Override
    public void write(DataOutput output, Date x) throws IOException {
        output.writeLong(x.getTime());
    }
}
