package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by macbook on 30.10.16.
 */
public class TestKeyValueStorage extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyDisabledKeyValueStorage<>(path,
                    new StringSerialization(), new StringSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyDisabledKeyValueStorage<>(path,
                    new IntegerSerialization(), new DoubleSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyDisabledKeyValueStorage<>(path,
                    new StudentKeySerialization(), new StudentValueSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

