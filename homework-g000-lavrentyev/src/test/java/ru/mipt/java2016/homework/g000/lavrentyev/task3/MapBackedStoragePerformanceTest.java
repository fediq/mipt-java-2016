package ru.mipt.java2016.homework.g000.lavrentyev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g000.lavrentyev.task2.MapBackedStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public class MapBackedStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MapBackedStorage<>(stringMaps.computeIfAbsent(path, p -> new HashMap<>()));
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MapBackedStorage<>(numbersMaps.computeIfAbsent(path, p -> new HashMap<>()));
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MapBackedStorage<>(pojoMaps.computeIfAbsent(path, p -> new HashMap<>()));
    }
}
