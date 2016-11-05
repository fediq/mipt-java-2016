package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by maria on 25.10.16.
 */
interface Serializer<Type> {

    /* сериализация и запись полученного значения в поток*/
    void serializeToStream(Type value, DataOutputStream outStream)  throws IOException;

    /* прочтение из потока и десериализация объекта */
    Type deserializeFromStream(DataInputStream inputStream) throws IOException;
}
