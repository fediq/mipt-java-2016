package ru.mipt.java2016.homework.g595.novikov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.novikov.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by igor on 11/14/16.
 */
public class MyCoolKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyCoolKeyValueStorage<String, String>(path,
                new StringSerialization(),
                new StringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyCoolKeyValueStorage<Integer, Double>(path,
                new IntSerialization(),
                new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyCoolKeyValueStorage<StudentKey, Student>(path,
                new StudentKeySerialization(),
                new StudentSerialization());
    }
}
