package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Надежда on 30.10.2016.
 */
public class KeyValueStorageTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MKeyValueStorage<String, String>(path, MSerialization.stringSerializer, MSerialization.stringSerializer);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MKeyValueStorage<Integer, Double>(path, MSerialization.integerSerializer, MSerialization.doubleSerializer);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MKeyValueStorage<StudentKey, Student>(path, MSerialization.studentKeySerializer, MSerialization.studentSerializer);
    }
}