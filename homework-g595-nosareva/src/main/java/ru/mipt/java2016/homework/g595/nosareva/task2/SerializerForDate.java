package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForDate implements Serializer<Date> {
    @Override
    public void serializeToStream(Date value, DataOutput outStream) throws IOException {
        outStream.writeLong(value.getTime());
    }

    @Override
    public Date deserializeFromStream(DataInput inputStream) throws IOException {
        return new Date(inputStream.readLong());
    }
}
