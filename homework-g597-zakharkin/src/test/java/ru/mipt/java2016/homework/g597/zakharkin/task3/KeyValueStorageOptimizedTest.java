package ru.mipt.java2016.homework.g597.zakharkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.zakharkin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.g597.zakharkin.task3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by ilya on 21.11.2016.
 */
public class KeyValueStorageOptimizedTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) { return new KeyValueStorageOptimized<>(path,
            StringSerializer.getInstance(),
            StringSerializer.getInstance()); }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) { return new KeyValueStorageOptimized<>(path,
            IntegerSerializer.getInstance(),
            DoubleSerializer.getInstance()); }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) { return new KeyValueStorageOptimized<>(path,
            StudentKeySerializer.getInstance(),
            StudentSerializer.getInstance()); }

}