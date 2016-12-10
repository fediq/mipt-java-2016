package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/*
 * Created by Vova Miller on 20.11.2016.
 */
public class SolidStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path)  {
        try {
            return new SolidStorageStrings(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new SolidStorageNumbers(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new SolidStorageStudents(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }
}