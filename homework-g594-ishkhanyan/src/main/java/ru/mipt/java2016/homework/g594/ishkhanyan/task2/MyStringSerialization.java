package ru.mipt.java2016.homework.g594.ishkhanyan.task2;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by ${Semien} on ${30.10.16}.
 */
public class MyStringSerialization implements MySerialization<String> {
    @Override
    public void writeToFile(String object, DataOutput file) throws IOException {
        file.writeUTF(object);
    }

    @Override
    public String readFromFile(DataInput file) throws IOException {
        return file.readUTF();
    }
}
