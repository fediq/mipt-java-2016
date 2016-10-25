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
        try {
            return new LazyMergedKeyValueStorage<>("String", "String", path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new LazyMergedKeyValueStorage<>("Integer", "Double", path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new LazyMergedKeyValueStorage<>("StudentKey", "Student", path);
        } catch (Exception e) {
            return null;
        }
    }

}
