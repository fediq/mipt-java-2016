package ru.mipt.java2016.homework.g597.nasretdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by isk on 31.10.16.
 */
public class StudentKeySerializer implements SerializerInterface<StudentKey> {
    @Override
    public void write(DataOutputStream stream, StudentKey studentKeyData) throws IOException {
        stream.writeInt(studentKeyData.getGroupId());
        stream.writeUTF(studentKeyData.getName());
    }

    @Override
    public StudentKey read(DataInputStream stream) throws IOException {
        return new StudentKey(stream.readInt(), stream.readUTF());
    }
}
