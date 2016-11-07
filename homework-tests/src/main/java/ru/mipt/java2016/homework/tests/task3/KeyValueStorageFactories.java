package ru.mipt.java2016.homework.tests.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public abstract class KeyValueStorageFactories {
    protected abstract KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException;

    protected abstract KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException;

    protected abstract KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException;
}
