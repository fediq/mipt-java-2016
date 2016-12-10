package ru.mipt.java2016.homework.g597.shirokova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

public class MyKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyKeyValueStorage<String, String> currentStorage = null;
        try {
            currentStorage = new MyKeyValueStorage<String, String>(path,
                    new ConcreteSerializationStrategy.StringConcreteStrategy(),
                    new ConcreteSerializationStrategy.StringConcreteStrategy());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return currentStorage;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyKeyValueStorage<Integer, Double> currentStorage = null;
        try {
            currentStorage = new MyKeyValueStorage<Integer, Double>(path,
                    new ConcreteSerializationStrategy.IntegerConcreteStrategy(),
                    new ConcreteSerializationStrategy.DoubleConcreteStrategy());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return currentStorage;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyKeyValueStorage<StudentKey, Student> currentStorage = null;
        try {
            currentStorage = new MyKeyValueStorage<StudentKey, Student>(path,
                    new ConcreteSerializationStrategy.StudentKeyConcreteStrategy(),
                    new ConcreteSerializationStrategy.StudentConcreteStrategy());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return currentStorage;
    }
}