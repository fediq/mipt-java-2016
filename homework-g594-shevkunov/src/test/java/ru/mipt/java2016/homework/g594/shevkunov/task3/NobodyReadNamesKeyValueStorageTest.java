package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by shevkunov on 14.11.16.
 */
public class NobodyReadNamesKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new NobodyReadNamesKeyValueStorage<>("String", "String", path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new NobodyReadNamesKeyValueStorage<>("Integer", "Double", path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new NobodyReadNamesKeyValueStorage<>("StudentKey", "Student", path);
        } catch (Exception e) {
            return null;
        }
    }
}
