package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author ulyanin
 * @since 31.10.16.
 */

public class LongSerializer implements Serializer<Long> {

    private static LongSerializer ourInstance = new LongSerializer();

    public static LongSerializer getInstance() {
        return ourInstance;
    }

    private LongSerializer() { }

    @Override
    public void serialize(Long data, DataOutput dataOutputStream) throws IOException {
        dataOutputStream.writeLong(data.longValue());
    }

    @Override
    public Long deserialize(DataInput dataInputStream) throws IOException {
        return dataInputStream.readLong();
    }
}
