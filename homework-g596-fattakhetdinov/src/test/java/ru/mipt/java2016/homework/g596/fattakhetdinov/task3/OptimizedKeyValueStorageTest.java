package ru.mipt.java2016.homework.g596.fattakhetdinov.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.DoubleSerializator;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.IntegerSerializator;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.StringSerializator;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.StudentKeySerializator;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.StudentSerializator;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

public class OptimizedKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path)
            throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path, new StringSerializator(),
                    new StringSerializator());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path)
            throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path, new IntegerSerializator(),
                    new DoubleSerializator());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path)
            throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path, new StudentKeySerializator(),
                    new StudentSerializator());
        } catch (IOException exception) {
            return null;
        }
    }
}