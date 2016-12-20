package ru.mipt.java.homework.g595.zueva.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
/**
 * Created by someb on 17.12.2016.
 */
public class FastKeyValueStorageTest extends KeyValueStoragePerformanceTest{
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new FastKeyValueStorageImpl(path, new FastStringSerializer(), new FastStringSerializer());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new FastKeyValueStorageImpl(path, new FastIntegerSerializer(), new FastDoubleSerializer());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new FastKeyValueStorageImpl(path, new FastStudentKeySerializer(), new FastStudentSerializer());
        } catch (IOException e) {
            return null;
        }
    }
}
}
