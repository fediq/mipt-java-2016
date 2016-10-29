package ru.mipt.java2016.homework.g595.yakusheva.task2;

import java.io.*;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyDoubleSerializer implements MyFirstSerializerInterface<Double> {

    @Override
    public void serializeToStream(DataOutputStream dataOutputStream, Double o) throws IOException {
        dataOutputStream.writeDouble(o);
    }

    @Override
    public Double deserializeFromStream(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readDouble();
    }

}
