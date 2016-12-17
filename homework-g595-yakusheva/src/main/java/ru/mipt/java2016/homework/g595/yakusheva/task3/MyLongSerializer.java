package ru.mipt.java2016.homework.g595.yakusheva.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Софья on 20.11.2016.
 */
public class MyLongSerializer implements MySecondSerializerInterface<Long> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, Long o) throws IOException {
        dataOutputStream.writeLong(o);
    }

    @Override
    public Long deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readLong();
    }
}
