package ru.mipt.java2016.homework.g597.kirilenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by Natak on 27.10.2016.
 */
public class OptimisedStorageTests extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyOptimisedStorage(path, SerializationType.SerializationString.getSerialization(),
                    SerializationType.SerializationString.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyOptimisedStorage(path, SerializationType.SerializationInteger.getSerialization(),
                    SerializationType.SerializationDouble.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyOptimisedStorage(path, SerializationStudentKey.getSerialization(),
                    SerializationStudent.getSerialization());
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }
}

