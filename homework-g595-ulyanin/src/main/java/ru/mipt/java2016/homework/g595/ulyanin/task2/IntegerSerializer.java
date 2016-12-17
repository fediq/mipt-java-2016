package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author ulyanin
 * @since 31.10.16.
 */

public class IntegerSerializer implements Serializer<Integer> {

    private static IntegerSerializer ourInstance = new IntegerSerializer();

    public static IntegerSerializer getInstance() {
        return ourInstance;
    }

    private IntegerSerializer() { }

    @Override
    public void serialize(Integer data, DataOutput dataOutputStream) throws IOException {
        dataOutputStream.writeInt(data.intValue());
    }

    @Override
    public Integer deserialize(DataInput dataInputStream) throws IOException {
        return dataInputStream.readInt();
    }
}
