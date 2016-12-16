package ru.mipt.java2016.homework.g595.yakusheva.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Софья on 26.10.2016.
 */
public interface MySecondSerializerInterface<Value> {

    void serializeToStream(DataOutputStream outputStream, Value value) throws IOException;

    Value deserializeFromStream(DataInputStream inputStream) throws IOException;
}
