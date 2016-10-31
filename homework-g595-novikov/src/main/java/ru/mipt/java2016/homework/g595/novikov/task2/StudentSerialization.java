package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by igor on 10/25/16.
 */
public class StudentSerialization extends MySerialization<Student> {
    @Override
    public void serialize(RandomAccessFile file, Student object) throws IOException {
        serializeStudent(file, object);
    }

    @Override
    public Student deserialize(RandomAccessFile file) throws IOException {
        return deserializeStudent(file);
    }
}
