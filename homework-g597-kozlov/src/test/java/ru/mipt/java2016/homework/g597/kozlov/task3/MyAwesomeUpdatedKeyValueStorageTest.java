package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class MyAwesomeUpdatedKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            MyAwesomeUpdatedKeyValueStorage test = new MyAwesomeUpdatedKeyValueStorage<String, String>(path,
                    new SerializationString(), new SerializationString());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            MyAwesomeUpdatedKeyValueStorage test = new MyAwesomeUpdatedKeyValueStorage<Integer, Double>(path,
                    new SerializationInteger(), new SerializationDouble());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            MyAwesomeUpdatedKeyValueStorage test = new MyAwesomeUpdatedKeyValueStorage<StudentKey, Student>(path,
                    new SerializationStudentKey(), new SerializationStudent());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}
