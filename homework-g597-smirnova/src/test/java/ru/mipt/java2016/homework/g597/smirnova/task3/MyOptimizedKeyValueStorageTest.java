package ru.mipt.java2016.homework.g597.smirnova.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;


/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class MyOptimizedKeyValueStorageTest extends KeyValueStoragePerformanceTest{

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path,
                    new StringSerializationStrategy(), new StringSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path,
                    new IntegerSerializationStrategy(), new DoubleSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new MyOptimizedKeyValueStorage<>(path,
                    new StudentKeySerializationStrategy(), new StudentSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }
}
