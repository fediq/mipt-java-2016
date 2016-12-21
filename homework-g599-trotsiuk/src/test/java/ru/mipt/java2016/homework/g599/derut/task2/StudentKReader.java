package ru.mipt.java2016.homework.g599.derut.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StudentKReader implements Serializer<StudentKey> {
    @Override
    public void write(RandomAccessFile f, StudentKey val) throws IOException {
        IntegerRead intS = new IntegerRead();
        StringReader stringS = new StringReader();
        intS.write(f, val.getGroupId());
        stringS.write(f, val.getName());

    }

    @Override
    public StudentKey read(RandomAccessFile f) throws IOException {
        IntegerRead intS = new IntegerRead();
        StringReader stringS = new StringReader();
        return new StudentKey(intS.read(f), stringS.read(f));
    }

}
