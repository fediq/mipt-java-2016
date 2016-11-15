package ru.mipt.java2016.homework.g597.smirnova.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Admin on 31.10.2016.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, new StringSerializationStrategy(), new StringSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, new IntegerSerializationStrategy(), new DoubleSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, new StudentKeySerializationStrategy(), new StudentSerializationStrategy());
        } catch (Exception e) {
            return null;
        }
    }

}
