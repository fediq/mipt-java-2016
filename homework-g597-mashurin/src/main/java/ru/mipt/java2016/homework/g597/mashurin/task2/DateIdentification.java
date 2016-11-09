package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.util.Date;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DateIdentification extends Identification<Date> {

    public static DateIdentification get() {
        return new DateIdentification();
    }

    @Override
    public void write(DataOutputStream output, Date object) throws IOException {
        output.writeLong(object.getTime());
    }

    @Override
    public Date read(DataInputStream input) throws IOException {
        return new Date(input.readLong());
    }

}
