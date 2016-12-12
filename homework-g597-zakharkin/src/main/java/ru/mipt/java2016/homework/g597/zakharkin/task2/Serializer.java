package ru.mipt.java2016.homework.g597.zakharkin.task2;

import java.io.*;

/**
 * Interface of data serializer
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public interface Serializer<Type> {
    void write(DataOutput file, Type data) throws IOException;

    Type read(DataInput file) throws IOException;
}
