package ru.mipt.java2016.homework.g595.kireev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Карим on 26.10.2016.
 */
public class MyDataBaseTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>("String", "String", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<Integer, Double>("Integer", "Double", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<StudentKey, Student>("StudentKey", "Student", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
