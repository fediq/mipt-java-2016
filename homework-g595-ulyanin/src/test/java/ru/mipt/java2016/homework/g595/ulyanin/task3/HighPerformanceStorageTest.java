package ru.mipt.java2016.homework.g595.ulyanin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.ulyanin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author ulyanin
 * @since 14.11.16.
 */

public class HighPerformanceStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        HighPerformancePreservingKeyValueStorage<String, String> result = null;
        try {
            result = new HighPerformancePreservingKeyValueStorage<>(
                    path,
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException | ValidationException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        HighPerformancePreservingKeyValueStorage<Integer, Double> result = null;
        try {
            result = new HighPerformancePreservingKeyValueStorage<>(
                    path,
                    IntegerSerializer.getInstance(),
                    DoubleSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException | ValidationException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        HighPerformancePreservingKeyValueStorage<StudentKey, Student> result = null;
        try {
            result = new HighPerformancePreservingKeyValueStorage<>(
                    path,
                    StudentKeySerializer.getInstance(),
                    StudentSerializer.getInstance()
            );
        } catch (IOException | NoSuchAlgorithmException | ValidationException e) {
            e.printStackTrace();
        }
        return result;
    }
}
