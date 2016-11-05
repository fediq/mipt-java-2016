package ru.mipt.java2016.homework.g597.komarov.task2;

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;

/**
 * Created by Михаил on 31.10.2016.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected  KeyValueStorage<String, String> buildStringsStorage(String path) {
        StringSerializer keySerializer = new StringSerializer();
        StringSerializer valueSerializer = new StringSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to DataBase");
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        IntegerSerializer keySerializer = new IntegerSerializer();
        DoubleSerializer valueSerializer = new DoubleSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to DataBase");
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        StudentKeySerializer keySerializer = new StudentKeySerializer();
        StudentSerializer valueSerializer = new StudentSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to DataBase");
        }
    }
}
