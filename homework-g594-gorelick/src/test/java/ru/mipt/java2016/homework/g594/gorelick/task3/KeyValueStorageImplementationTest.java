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
                    new StringFileWorker(), new StringFileWorker());
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
                    new IntegerFileWorker(), new DoubleFileWorker());
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
                    new StudentKeyFileWorker(), new StudentFileWorker());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}
