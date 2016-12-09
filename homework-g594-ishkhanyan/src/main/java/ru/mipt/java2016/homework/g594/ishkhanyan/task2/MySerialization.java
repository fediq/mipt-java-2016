package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public interface MySerialization<Type> {
    void writeToFile(Type object, DataOutput file) throws IOException;

    Type readFromFile(DataInput file) throws IOException;
}


