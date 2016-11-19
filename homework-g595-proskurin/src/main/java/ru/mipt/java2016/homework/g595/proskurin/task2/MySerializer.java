package ru.mipt.java2016.homework.g595.proskurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface MySerializer<V> {
    void output(DataOutputStream out, V val) throws IOException;

    V input(DataInputStream in) throws IOException;
}
