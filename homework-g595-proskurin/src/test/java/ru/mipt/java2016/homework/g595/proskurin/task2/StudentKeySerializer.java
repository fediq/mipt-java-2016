package ru.mipt.java2016.homework.g595.proskurin.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StudentKeySerializer implements MySerializer<StudentKey> {
    public void output(DataOutputStream out, StudentKey val) throws IOException {
        out.writeInt(val.getGroupId());
        out.writeUTF(val.getName());
    }

    public StudentKey input(DataInputStream in) throws IOException {
        StudentKey tmp = new StudentKey(
                in.readInt(),
                in.readUTF()
        );
        return tmp;
    }
}
