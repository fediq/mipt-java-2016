package ru.mipt.java2016.homework.g597.grishutin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.grishutin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;


public class LargeKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new LargeKeyValueStorage<>(path,
                    new StringSerializer(),
                    new StringSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new LargeKeyValueStorage<>(path,
                    new IntegerSerializer(),
                    new DoubleSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new LargeKeyValueStorage<>(path,
                    new StudentKeySerializationStrategy(),
                    new StudentSerializationStrategy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
