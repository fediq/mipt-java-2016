package ru.mipt.java2016.homework.g594.pyrkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.*;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * binary hash-map KeyValueStorageTest
 * Created by randan on 10/30/16.
 */

public class KeyValueStorageImplementationTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path,
                    new StringSerializer(), new StringSerializer());
        }catch (IOException e){
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path, new IntegerSerializer(),
                    new DoubleSerializer());
        }catch (IOException e){
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path, new StudentKeySerializer(),
                    new StudentSerializer());
        }catch (IOException e){
            return null;
        }
    }
}
