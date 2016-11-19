package ru.mipt.java2016.homework.g595.novikov.task3;

import ru.mipt.java2016.homework.g595.novikov.task2.MySerialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 11/5/16.
 */
public class LongSerialization extends MySerialization<Long> {
    @Override
    public void serialize(DataOutput file, Long object) throws IOException {
        file.writeLong(object);
    }

    @Override
    public Long deserialize(DataInput file) throws IOException {
        return file.readLong();
    }

    @Override
    public long getSizeSerialized(Long object) {
        return getSizeSerializedLong(object);
    }
}
