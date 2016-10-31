package ru.mipt.java2016.homework.g596.kravets.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface MySerialization<T> {
    T read(DataInputStream input) throws IOException;

    void write(DataOutputStream output, T data) throws IOException;
}

