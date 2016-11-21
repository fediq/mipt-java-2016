package ru.mipt.java2016.homework.g594.glebov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.glebov.task2.MySerializer;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daniil on 21.11.16.
 */
public class SSTableIndexStorageTest extends KeyValueStoragePerformanceTest{
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new SSTableIndexStorage<>(path, MySerializer.STRING, MySerializer.STRING);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new SSTableIndexStorage<>(path, MySerializer.INT, MySerializer.DOUBLE);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new SSTableIndexStorage<>(path, new StudentKeySerialize(), new StudentSerialize());
    }

}
