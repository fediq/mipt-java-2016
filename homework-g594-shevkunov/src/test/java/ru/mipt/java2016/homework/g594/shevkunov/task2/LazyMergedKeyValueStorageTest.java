package ru.mipt.java2016.homework.g594.shevkunov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Testing class for LazyMergedKeyValueStorage
 * Created by shevkunov on 22.10.16.
 */
public class LazyMergedKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new LazyMergedKeyValueStorage(path);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildI2DStorage(String path) {
        return new LazyMergedKeyValueStorage(path);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path) {
        return new LazyMergedKeyValueStorage(path);
    }
}
