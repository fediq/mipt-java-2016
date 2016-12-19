package ru.mipt.java2016.homework.g595.ferenets.task3;

import ru.mipt.java2016.homework.base.task2.*;
import ru.mipt.java2016.homework.g595.ferenets.task2.*;
import ru.mipt.java2016.homework.tests.task2.*;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class OptimizedStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedStorage(path,
                    new StringSerializationStrategy(), new StringSerializationStrategy());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedStorage(path,
                    new IntegerSerializationStrategy(), new StringSerializationStrategy());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedStorage(path,
                    new StudentKeySerializationStrategy(), new StudentSerializationStrategy());
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }
}
