package ru.mipt.java2016.homework.g595.proskurin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class MapBackedStoragePerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MapBackedStorage<String, String>(path, new StringSerializer(), new StringSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MapBackedStorage<Integer, Double>(path, new IntegerSerializer(), new DoubleSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MapBackedStorage<StudentKey, Student>(path, new StudentKeySerializer(), new StudentSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }
}
