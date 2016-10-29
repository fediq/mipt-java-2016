package ru.mipt.java2016.homework.g595.yakusheva.task2;

import java.io.*;

/**
 * Created by Софья on 26.10.2016.
 */
public interface MyFirstSerializerInterface<Value> {

    void serializeToStream(DataOutputStream outputStream, Value value) throws IOException;

    Value deserializeFromStream(DataInputStream inputStream) throws IOException;
}
