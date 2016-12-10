package ru.mipt.java2016.homework.g597.smirnova.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class StudentKeySerializationStrategy implements SerializationStrategy<StudentKey>{
    @Override
    public void writeToStream(DataOutput s, StudentKey value) throws IOException {
        s.writeInt(value.getGroupId());
        s.writeUTF(value.getName());
    }

    @Override
    public StudentKey readFromStream(DataInput s) throws IOException {
        return new StudentKey(s.readInt(), s.readUTF());
    }
}
