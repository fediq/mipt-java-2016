package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * StudentKeySerializer
 * Created by randan on 10/30/16.
 */
public class StudentKeySerializer implements SerializerInterface<StudentKey> {

    @Override
    public String serialize(StudentKey object) {
        return object.toString();
    }

    @Override
    public StudentKey deserialize(String inputString) {
        return null;
    }
}
