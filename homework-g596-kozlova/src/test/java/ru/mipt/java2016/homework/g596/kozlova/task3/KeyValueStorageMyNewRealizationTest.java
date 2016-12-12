package ru.mipt.java2016.homework.g596.kozlova.task3;

import org.junit.Ignore;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.util.DoubleSummaryStatistics;

public class KeyValueStorageMyNewRealizationTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new KeyValueStorageMyNewRealization<>(path,
                    new MyStringSerialization(), new MyStringSerialization());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new KeyValueStorageMyNewRealization<>(path,
                    new MyIntegerSerialization(), new MyDoubleSerialization());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new KeyValueStorageMyNewRealization<>(path,
                    new MyStudentKeySerialization(), new MyStudentSerialization());
        } catch (IOException e) {
            return null;
        }
    }
}