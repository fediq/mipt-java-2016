package ru.mipt.java2016.homework.g595.ferenets.task2;


import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StudentKeySerializationStrategy implements SerializationStrategy<StudentKey> {
    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        return new StudentKey(file.readInt(), file.readUTF());
    }

    @Override
    public void write(RandomAccessFile file, StudentKey value) throws IOException {
        file.writeInt(value.getGroupId());
        file.writeUTF(value.getName());
    }
}
