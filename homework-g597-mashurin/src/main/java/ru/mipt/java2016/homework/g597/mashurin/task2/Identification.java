package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

abstract class Identification<Value> {
    abstract Value read(DataInputStream input) throws IOException;

    abstract void write(DataOutputStream output, Value value) throws IOException;
}
