package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ${Semien} on ${30.10.16}.
 */
public class MyStringSerialization implements MySerialization<String> {
    @Override
    public void writeToFile(String object, DataOutputStream file) throws IOException {
        file.writeUTF(object);
    }

    @Override
    public String readFromFile(DataInputStream file) throws IOException {
        return file.readUTF();
    }
}
