package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.pyrkin.task2.serializer.*;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by randan on 11/16/16.
 */
public class SSTableKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new SSTableKeyValueStorage<>(path,
                    new StringSerializer(), new StringSerializer(), 100);
        }catch (IOException e){
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new SSTableKeyValueStorage<>(path, new IntegerSerializer(),
                    new DoubleSerializer(), 100);
        }catch (IOException e){
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new SSTableKeyValueStorage<>(path, new StudentKeySerializer(),
                    new StudentSerializer(), 100);
        }catch (IOException e){
            return null;
        }
    }
}
