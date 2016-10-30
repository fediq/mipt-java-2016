package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MySerialization<T> {
    abstract T read(DataInputStream read_from_file) throws IOException;
    abstract void write(DataOutputStream write_to_file, T object) throws IOException;
}
