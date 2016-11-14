package ru.mipt.java2016.homework.g595.ulyanin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g595.ulyanin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ulyanin on 14.11.16.
 */
public class HighPerformanceStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        MapPreservingStorage<String, String> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        MapPreservingStorage<Integer, Double> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    IntegerSerializer.getInstance(),
                    DoubleSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        MapPreservingStorage<StudentKey, Student> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    StudentKeySerializer.getInstance(),
                    StudentSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
