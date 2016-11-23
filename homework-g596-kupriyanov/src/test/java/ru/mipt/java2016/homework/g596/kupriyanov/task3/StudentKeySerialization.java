package ru.mipt.java2016.homework.g596.kupriyanov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Created by Artem Kupriyanov on 29/10/2016.
 */
public class StudentKeySerialization implements SerializationStrategy<StudentKey> {

    @Override
    public void write(StudentKey value, RandomAccessFile out) throws IOException {
        out.writeInt(value.getGroupId());
        out.writeUTF(value.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile in) throws IOException {
        StudentKey responceStudentKey = new StudentKey(in.readInt(), in.readUTF());
        return responceStudentKey;
    }
}
