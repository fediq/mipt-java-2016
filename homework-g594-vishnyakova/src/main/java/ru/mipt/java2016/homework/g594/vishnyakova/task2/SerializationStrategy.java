package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Nina on 27.10.16.
 */
public abstract class SerializationStrategy<T> {
    abstract T read(DataInputStream rd) throws IOException;

    abstract void write(DataOutputStream wr, T obj) throws IOException;
}
