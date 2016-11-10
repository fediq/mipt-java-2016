package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author ulyanin
 * @sinse 31.10.16.
 */

public interface Serializer<DataType> {

    void serialize(DataType data, DataOutputStream dataOutputStream) throws IOException;

    DataType deserialize(DataInputStream dataInputStream) throws IOException;

}
