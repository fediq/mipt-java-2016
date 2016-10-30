package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * StudentSerializer
 * Created by randan on 10/30/16.
 */
public class StudentSerializer implements SerializerInterface<Student>{

    @Override
    public String serialize(Student object) {
        return object.toString();
    }

    @Override
    public Student deserialize(String inputString) {
        return null;
    }
}
