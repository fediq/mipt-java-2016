package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by Admin on 31.10.2016.
 */
public class StudentSerializationStrategy implements SerializationStrategy<Student> {
    @Override
    public void writeToStream(DataOutputStream s, Student value) throws IOException {

    }

    @Override
    public Student readFromStream(DataInputStream s) throws IOException {
        return null;
    }
}
