package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

interface Serializer<K> {

    void serializeWrite(K value, DataOutput stream)  throws IOException;

    K deserializeRead(DataInput stream) throws IOException;
}
