package ru.mipt.java2016.homework.g595.yakusheva.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Софья on 26.10.2016.
 */
public class MyStringSerializer implements MySecondSerializerInterface<String> {
    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, String o) throws IOException {
        dataOutputStream.writeUTF(o);
    }

    @Override
    public String deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}
