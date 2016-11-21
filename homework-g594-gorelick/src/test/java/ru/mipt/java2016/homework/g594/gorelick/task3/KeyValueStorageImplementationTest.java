package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import java.io.IOException;

public class KeyValueStorageImplementationTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new StringSerializer(), new StringSerializer());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new IntegerSerializer(), new DoubleSerializer());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new StudentKeySerializer(), new StudentSerializer());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}
