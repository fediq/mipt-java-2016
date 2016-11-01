package ru.mipt.java2016.homework.g597.kirilenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization.*;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Natak on 27.10.2016.
 */
public class MyStorageTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyStorage(path, SerializationType.SerializationString.getSerialization(),
                    SerializationType.SerializationString.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyStorage(path, SerializationType.SerializationInteger.getSerialization(),
                    SerializationType.SerializationDouble.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyStorage(path, SerializationStudentKey.getSerialization(),
                    SerializationStudent.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }
}

