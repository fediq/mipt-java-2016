package ru.mipt.java2016.homework.g594.plahtinskiy.task3;


import ru.mipt.java2016.homework.g594.plahtinskiy.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by VadimPl on 21.11.16.
 */
public class MyKeyValueStorageNewTest extends KeyValueStoragePerformanceTest {
    @Override
    protected MyKeyValueStorageNew<String, String> buildStringsStorage(String path) {
        try {
            SerializationString serialization = new SerializationString();
            return new MyKeyValueStorageNew<String, String>(path, "storage", serialization, serialization);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected MyKeyValueStorageNew<Integer, Double> buildNumbersStorage(String path) {
        try {
            SerializationInt serialization1 = new SerializationInt();
            SerializationDouble serialization2 = new SerializationDouble();
            return new MyKeyValueStorageNew<Integer, Double>(path, "storage", serialization1, serialization2);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected MyKeyValueStorageNew<StudentKey, Student> buildPojoStorage(String path) {
        try {
            SerializationStudentKey serialization1 = new SerializationStudentKey();
            SerializationStudent serialization2 = new SerializationStudent();
            return new MyKeyValueStorageNew<StudentKey, Student>(path, "storage.db", serialization1, serialization2);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
