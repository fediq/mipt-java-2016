package ru.mipt.java2016.homework.g595.yakusheva.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Софья on 27.10.2016.
 */
public class MySecondKeyValueStorageTest extends AbstractSingleFileStorageTest

{
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MySecondKeyValueStorage<String, String>(path, new MyStringSerializer(), new MyStringSerializer(), 0);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MySecondKeyValueStorage<Integer, Double>(path, new MyDoubleSerializer.MyIntegerSerializer(), new MyDoubleSerializer(), 0);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MySecondKeyValueStorage<StudentKey, Student>(path, new MyStudentKeySerializer(), new MyStudentValueSerializer(), 0);
    }
}
