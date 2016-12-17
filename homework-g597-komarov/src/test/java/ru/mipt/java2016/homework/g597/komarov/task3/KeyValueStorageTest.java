package ru.mipt.java2016.homework.g597.komarov.task3;

import ru.mipt.java2016.homework.g597.komarov.task2.*;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by mikhail on 22.11.16.
 */
public class KeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    public KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        StringSerializer keySerializer = new StringSerializer();
        StringSerializer valueSerializer = new StringSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new MalformedDataException("Something went wrong");
        }
    }

    @Override
    public KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        IntegerSerializer keySerializer = new IntegerSerializer();
        DoubleSerializer valueSerializer = new DoubleSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new MalformedDataException("Something went wrong");
        }
    }

    @Override
    public KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        StudentKeySerializer keySerializer = new StudentKeySerializer();
        StudentSerializer valueSerializer = new StudentSerializer();
        try {
            return new MyKeyValueStorage<>(path, keySerializer, valueSerializer);
        } catch (IOException e) {
            throw new MalformedDataException("Something went wrong");
        }
    }
}
