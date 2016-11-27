package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Howl on 30.10.2016.
 */
public class DateParser implements ItemParser<Date> {

    @Override
    public void serialize(Date arg, FileOutputStream out) throws IOException {
        new LongParser().serialize(arg.getTime(), out);
    }

    @Override
    public Date deserialize(FileInputStream in) throws IOException {
        return new Date(new LongParser().deserialize(in));
    }
}
