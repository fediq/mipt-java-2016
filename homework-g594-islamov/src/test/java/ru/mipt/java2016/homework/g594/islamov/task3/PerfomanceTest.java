package ru.mipt.java2016.homework.g594.islamov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

public class PerfomanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new UpgradedKVStorage(path, new KVStorageStringSerializer(),
                new KVStorageStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new UpgradedKVStorage(path, new KVStorageIntegerSerializer(),
                new KVStorageDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new UpgradedKVStorage(path, new KVStorageStudentKeySerializer(),
                new KVStorageStudentSerializer());
    }
}
