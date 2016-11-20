package ru.mipt.java2016.homework.g597.mashurin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    public KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage(path, StringIdentification.get(), StringIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage(path, IntegerIdentification.get(), DoubleIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage(path, StudentKeyIdentification.get(), StudentIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }
}
