package ru.mipt.java2016.homework.g595.yakusheva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyFirstKeyValueStorageTest extends AbstractSingleFileStorageTest

{
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyFirstKeyValueStorage<>(path, new MyStringSerializer(), new MyStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyFirstKeyValueStorage<>(path, new MyIntegerSerializer(), new MyDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyFirstKeyValueStorage<StudentKey, Student>(path, new MyStudentKeySerializer(), new MyStudentValueSerializer());
    }
}
