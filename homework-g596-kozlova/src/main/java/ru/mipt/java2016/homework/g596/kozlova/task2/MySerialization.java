package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MySerialization<T> {
    abstract T read(DataInputStream readFromFile) throws IOException;
    
    abstract void write(DataOutputStream writeToFile, T object) throws IOException;
}
