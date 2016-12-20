package ru.mipt.java2016.homework.g594.anukhin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.anukhin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public class MapBackedStoragePerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KeyValueStorageImpl<String, String>(path, new StringSerializableImpl(),
                new StringSerializableImpl());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KeyValueStorageImpl<Integer, Double>(path, new IntegerSerializableImpl(),
                new DoubleSerializableImpl());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KeyValueStorageImpl<StudentKey, Student>(path, new StudentKeySerializableImpl(),
                new StudentSerializableImpl());
    }
}
