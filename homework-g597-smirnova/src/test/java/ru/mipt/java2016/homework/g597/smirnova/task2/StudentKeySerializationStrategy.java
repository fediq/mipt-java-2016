package ru.mipt.java2016.homework.g597.smirnova.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class StudentKeySerializationStrategy implements SerializationStrategy<StudentKey> {
    @Override
    public void writeToStream(DataOutputStream s, StudentKey value) throws IOException {
        s.writeInt(value.getGroupId());
        s.writeUTF(value.getName());
    }

    @Override
    public StudentKey readFromStream(DataInputStream s) throws IOException {
        return new StudentKey(s.readInt(), s.readUTF());
    }
}
