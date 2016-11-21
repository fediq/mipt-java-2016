package ru.mipt.java2016.homework.g595.yakusheva.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyIntegerSerializer implements MySecondSerializerInterface<Integer> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, Integer o) throws IOException {
        dataOutputStream.writeInt(o);
    }

    @Override
    public Integer deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readInt();
    }
}
