package ru.mipt.java2016.homework.g595.proskurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerSerializer implements MySerializer<Integer> {
    public void output(DataOutputStream out, Integer val) throws IOException {
        out.writeInt(val);
    }

    public Integer input(DataInputStream in) throws  IOException {
        return in.readInt();
    }
}
