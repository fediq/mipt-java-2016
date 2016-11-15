package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;

/**
 * Created by Vova Miller on 31.10.2016.
 */

public class MillerStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path)  {
        try {
            return new MillerStorageStrings(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MillerStorageNumbers(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MillerStorageStudents(path);
        } catch (IOException e) {
            throw new RuntimeException("NotDirectoryException", e);
        }
    }
}