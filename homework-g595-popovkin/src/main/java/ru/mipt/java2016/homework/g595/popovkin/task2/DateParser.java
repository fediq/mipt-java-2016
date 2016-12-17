package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.*;
import java.util.Date;

/**
 * Created by Howl on 30.10.2016.
 */
public class DateParser implements ItemParser<Date> {

    @Override
    public void serialize(Date arg, OutputStream out) throws IOException {
        new LongParser().serialize(arg.getTime(), out);
    }

    @Override
    public Date deserialize(InputStream in) throws IOException {
        return new Date(new LongParser().deserialize(in));
    }
}
