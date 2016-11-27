package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 25.11.2016.
 */


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class PerfomanceMapTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new OptimizedHashTable<>(path,
                    new StringSerializator(), new StringSerializator());
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new OptimizedHashTable<>(path,
                    new IntegerSerializator(), new DoubleSerializator());
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new OptimizedHashTable<>(path,
                    new StudentKeySerializator(), new StudentSerializator());
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}
