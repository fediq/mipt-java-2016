package ru.mipt.java2016.homework.g594.glebov.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daniil on 31.10.16.
 */
public class KeyValueStorageTest extends AbstractSingleFileStorageTest {
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<>(path, MySerializer.STRING, MySerializer.STRING);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage<>(path, MySerializer.INT, MySerializer.DOUBLE);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage<>(path, new StudentKeySerialize(), new StudentSerialize());
    }
}