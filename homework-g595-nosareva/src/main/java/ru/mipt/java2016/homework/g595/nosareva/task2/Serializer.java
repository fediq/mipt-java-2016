package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.*;


/**
 * Created by maria on 25.10.16.
 */
public interface Serializer<Type> {

    /* сериализация и запись полученного значения в поток*/
    void serializeToStream(Type value, DataOutput outStream)  throws IOException;

    /* прочтение из потока и десериализация объекта */
    Type deserializeFromStream(DataInput inputStream) throws IOException;
}
