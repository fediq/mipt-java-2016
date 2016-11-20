package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class KeyValueStorageMyRealizationTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KeyValueStorageMyRealization<>(path, new MyStringSerialization(), new MyStringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KeyValueStorageMyRealization(path, new MyIntegerSerialization(), new MyDoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KeyValueStorageMyRealization(path, new MyStudentKeySerialization(), new MyStudentSerialization());
    }
}