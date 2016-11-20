package ru.mipt.java2016.homework.g595.proskurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerializer implements MySerializer<String> {
    public void output(DataOutputStream out, String val) throws IOException {
        out.writeUTF(val);
    }

    public String input(DataInputStream in) throws IOException {
        return in.readUTF();
    }
}
