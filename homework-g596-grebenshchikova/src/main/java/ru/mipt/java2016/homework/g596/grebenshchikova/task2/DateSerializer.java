package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by liza on 31.10.16.
 */
public class DateSerializer implements MySerializerInterface<Date> {
    @Override
    public void write(DataOutput output, Date object) throws IOException {
        output.writeLong(object.getTime());
    }

    @Override
    public Date read(DataInput input) throws IOException {
        return new Date(input.readLong());
    }

    private static final DateSerializer EXAMPLE = new DateSerializer();

    public static DateSerializer getExample() {
        return EXAMPLE;
    }

    private DateSerializer() {
    }
}

