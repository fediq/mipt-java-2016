package ru.mipt.java2016.homework.g597.dmitrieva.task3;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g597.dmitrieva.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by irinadmitrieva on 21.11.16.
 */
public class OptimizedStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new OptimizedKeyValueStorage<>(path,
                    new StringSerialization(), new StringSerialization());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new OptimizedKeyValueStorage<>(path,
                    new IntegerSerialization(), new DoubleSerialization());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new OptimizedKeyValueStorage<>(path,
                    new StudentKeySerialization(), new StudentValueSerialization());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }
}
