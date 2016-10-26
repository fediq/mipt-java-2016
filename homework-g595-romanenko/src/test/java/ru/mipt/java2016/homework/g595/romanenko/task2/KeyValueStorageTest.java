package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.*;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * KeyValueStorageTest
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/

public class KeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyKeyValueStorage<String, String> result = null;
        try {
            result = new MyKeyValueStorage<>(
                    path,
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyKeyValueStorage<Integer, Double> result = null;
        try {
            result = new MyKeyValueStorage<>(
                    path,
                    IntegerSerializer.getInstance(),
                    DoubleSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyKeyValueStorage<StudentKey, Student> result = null;
        try {
            result = new MyKeyValueStorage<>(
                    path,
                    StudentKeySerializer.getInstance(),
                    StudentSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
