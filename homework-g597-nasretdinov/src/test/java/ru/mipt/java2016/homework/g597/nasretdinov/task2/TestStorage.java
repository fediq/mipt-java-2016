package ru.mipt.java2016.homework.g597.nasretdinov.task2;

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by isk on 31.10.16.
 */
public class TestStorage extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new Storage<>(path, new StringSerializer(), new StringSerializer());
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new Storage<>(path, new IntegerSerializer(), new DoubleSerializer());
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new Storage<>(path, new StudentKeySerializer(), new StudentSerializer());
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        return null;
    }
}
