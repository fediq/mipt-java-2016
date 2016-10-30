package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class StudentKeySerialization extends Serialization<StudentKey>{
    @Override
    public StudentKey read(DataInputStream input) throws IOException {
        return new StudentKey(input.readInt(), input.readUTF());
    }

    @Override
    public void write(DataOutputStream output, StudentKey x) throws IOException {
        output.writeInt(x.getGroupId());
        output.writeUTF(x.getName());
    }
}
