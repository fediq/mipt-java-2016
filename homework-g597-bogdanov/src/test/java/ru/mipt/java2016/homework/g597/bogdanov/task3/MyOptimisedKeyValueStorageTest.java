package ru.mipt.java2016.homework.g597.bogdanov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.bogdanov.task2.IntegerDoubleSerializationStrategy;
import ru.mipt.java2016.homework.g597.bogdanov.task2.StringStringSerializationStrategy;
import ru.mipt.java2016.homework.g597.bogdanov.task2.StudentKeyStudentSerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class MyOptimisedKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyOptimisedKeyValueStorage<>(path,
                    StringStringSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyOptimisedKeyValueStorage<>(path, IntegerDoubleSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyOptimisedKeyValueStorage<>(path, StudentKeyStudentSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
