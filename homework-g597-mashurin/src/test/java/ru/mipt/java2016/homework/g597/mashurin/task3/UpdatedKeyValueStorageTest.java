package ru.mipt.java2016.homework.g597.mashurin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class UpdatedKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    public KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new UpdatedKeyValueStorage(path, StringIdentification.get(), StringIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new UpdatedKeyValueStorage(path, IntegerIdentification.get(), DoubleIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new UpdatedKeyValueStorage(path, StudentKeyIdentification.get(), StudentIdentification.get());
        }
        catch (IOException e){
            return null;
        }
    }
}
