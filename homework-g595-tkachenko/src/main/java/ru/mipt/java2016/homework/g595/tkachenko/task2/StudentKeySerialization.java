package ru.mipt.java2016.homework.g595.tkachenko.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class StudentKeySerialization extends Serialization<StudentKey> {
    @Override
    public StudentKey read(DataInputStream input) throws IOException {
        return new StudentKey(input.readInt(), readString(input));
    }

    @Override
    public void write(DataOutputStream output, StudentKey x) throws IOException {
        output.writeInt(x.getGroupId());
        writeString(output, x.getName());
    }
}
