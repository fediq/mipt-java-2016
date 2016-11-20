package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

public class KeyValueStorageMyNewRealizationTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new KeyValueStorageMyNewRealization(path, new MyStringSerialization(), new MyStringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new KeyValueStorageMyNewRealization(path, new MyIntegerSerialization(), new MyDoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new KeyValueStorageMyNewRealization(path, new MyStudentKeySerialization(), new MyStudentSerialization());
    }
}