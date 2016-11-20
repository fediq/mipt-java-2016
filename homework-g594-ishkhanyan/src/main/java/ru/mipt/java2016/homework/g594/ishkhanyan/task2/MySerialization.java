package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public interface MySerialization<Type> {
    void writeToFile(Type object, DataOutputStream file) throws IOException;

    void writeToFile(Type object, RandomAccessFile file) throws IOException;

    Type readFromFile(DataInputStream file) throws IOException;

    Type readFromFile(RandomAccessFile file) throws IOException;
}


