package ru.mipt.java2016.homework.g594.stepanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class KeyValueStorageTestImplementation extends AbstractSingleFileStorageTest{
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KeyValueStorageImplementation(path, "String", "String");
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KeyValueStorageImplementation(path, "Integer", "Double");
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KeyValueStorageImplementation(path, "StudentKey", "Student");
    }
}
