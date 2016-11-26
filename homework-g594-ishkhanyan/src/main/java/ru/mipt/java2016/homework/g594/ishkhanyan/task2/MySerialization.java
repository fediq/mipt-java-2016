package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


interface MySerialization<Type> {
    void writeToFile(Type object, DataOutputStream file) throws IOException;

    Type readFromFile(DataInputStream file) throws IOException;
}


