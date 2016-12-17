package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author ulyanin
 * @sinse 31.10.16.
 */

public interface Serializer<DataType> {

    void serialize(DataType data, DataOutput dataOutput) throws IOException;

    DataType deserialize(DataInput dataInput) throws IOException;

}
