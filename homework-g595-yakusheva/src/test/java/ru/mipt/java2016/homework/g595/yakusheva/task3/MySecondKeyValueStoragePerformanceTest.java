package ru.mipt.java2016.homework.g595.yakusheva.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Софья on 12.11.2016.
 */
public class MySecondKeyValueStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new MySecondKeyValueStorage<String, String>(path, new MyStringSerializer(), new MyStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new MySecondKeyValueStorage<Integer, Double>(path, new MyIntegerSerializer(), new MyDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new MySecondKeyValueStorage<StudentKey, Student>(path, new MyStudentKeySerializer(), new MyStudentValueSerializer());
    }

    /*@Test
    @Ignore
    public void measure100kWDump100kR() {}*/
}
