package ru.mipt.java2016.homework.g597.spirin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Created by whoami on 11/22/16.
 */
public class HighPerformanceKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    private final String filename = "storage.db";

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new HighPerformanceKeyValueStorage<>(path, filename,
                    StringSerializer.getInstance(), StringSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new HighPerformanceKeyValueStorage<>(path, filename,
                    IntegerSerializer.getInstance(), DoubleSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new HighPerformanceKeyValueStorage<>(path, filename,
                    StudentKeySerializer.getInstance(), StudentSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
