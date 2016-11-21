package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Тесты.
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyAwesomeKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            MyAwesomeKeyValueStorage test = new MyAwesomeKeyValueStorage<String, String>(path,
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
            MyAwesomeKeyValueStorage test = new MyAwesomeKeyValueStorage<Integer, Double>(path,
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
            MyAwesomeKeyValueStorage test = new MyAwesomeKeyValueStorage<StudentKey, Student>(path,
                    new SerializationStudentKey(), new SerializationStudent());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}
