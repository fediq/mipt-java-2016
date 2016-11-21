package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Stanislav A. Litvinov
 * @version 1.0.0
 * @since 31.10.16.
 */
public interface MySerialization<T> {
    T read(DataInputStream file) throws IOException;

    void write(DataOutputStream file, T object) throws IOException;
}
