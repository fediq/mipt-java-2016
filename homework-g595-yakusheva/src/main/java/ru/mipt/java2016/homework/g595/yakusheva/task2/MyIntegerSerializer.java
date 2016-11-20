package ru.mipt.java2016.homework.g595.yakusheva.task2;

import java.io.*;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyIntegerSerializer implements MyFirstSerializerInterface<Integer> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, Integer o) throws IOException {
        dataOutputStream.writeInt(o);
    }

    @Override
    public Integer deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readInt();
    }
}
