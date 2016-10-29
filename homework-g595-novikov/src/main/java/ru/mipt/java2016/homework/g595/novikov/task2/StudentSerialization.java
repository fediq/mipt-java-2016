package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/25/16.
 */
public class StudentSerialization extends MySerialization<Student> {
    @Override
    public void serialize(DataOutput file, Student object) throws IOException {
        serializeStudent(file, object);
    }

    @Override
    public Student deserialize(DataInput file) throws IOException {
        return deserializeStudent(file);
    }
}
