package ru.mipt.java2016.homework.g595.yakusheva.task2;

import java.io.*;

/**
 * Created by Софья on 26.10.2016.
 */
public class MyStringSerializer implements MyFirstSerializerInterface<String> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, String o) throws IOException {
        dataOutputStream.writeUTF(o);
    }

    @Override
    public String deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}
