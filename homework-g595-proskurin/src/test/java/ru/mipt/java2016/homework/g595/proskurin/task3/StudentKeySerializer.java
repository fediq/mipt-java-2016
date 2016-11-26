package ru.mipt.java2016.homework.g595.proskurin.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StudentKeySerializer implements MySerializer<StudentKey> {
    public void output(RandomAccessFile out, StudentKey val) throws IOException {
        out.writeInt(val.getGroupId());
        out.writeUTF(val.getName());
    }

    public StudentKey input(RandomAccessFile in) throws IOException {
        StudentKey tmp = new StudentKey(
                in.readInt(),
                in.readUTF()
        );
        return tmp;
    }
}
