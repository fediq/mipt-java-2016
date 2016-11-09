package ru.mipt.java2016.homework.g595.proskurin.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(path, new StringSerializer(), new StringSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<Integer, Double>(path, new IntegerSerializer(), new DoubleSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<StudentKey, Student>(path, new StudentKeySerializer(), new StudentSerializer());
        }
        catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        return null;
    }
}
