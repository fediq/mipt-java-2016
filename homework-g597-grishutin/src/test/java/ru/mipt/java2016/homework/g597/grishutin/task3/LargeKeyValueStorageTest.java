package ru.mipt.java2016.homework.g597.grishutin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g597.grishutin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;


public class LargeKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new LargeKeyValueStorage<>(path,
                    new StringSerializationStrategy(),
                    new StringSerializationStrategy());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new LargeKeyValueStorage<>(path,
                    new IntegerSerializationStrategy(),
                    new DoubleSerializationStrategy());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new LargeKeyValueStorage<>(path,
                    new StudentKeySerializationStrategy(),
                    new StudentSerializationStrategy());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
