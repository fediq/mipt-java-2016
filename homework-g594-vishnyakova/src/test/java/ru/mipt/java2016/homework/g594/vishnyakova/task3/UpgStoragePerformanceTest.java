package ru.mipt.java2016.homework.g594.vishnyakova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Nina on 18.11.16.
 */
public class UpgStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new UpgKeyValueStorage("String:String\n", path,
                new StringNewSerializationStrategy(), new StringNewSerializationStrategy());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new UpgKeyValueStorage("Integer:Double\n", path,
                new IntegerNewSerializationStrategy(), new DoubleNewSerializationStrategy());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new UpgKeyValueStorage("StudentKey:Student\n", path,
                new StudentKeyNewSerializationStrategy(), new StudentNewSerializationStrategy());
    }
}
