package ru.mipt.java2016.homework.g596.kravets.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MySerialization<T> { // Интерфейс
    abstract T read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, T data) throws IOException;
}

