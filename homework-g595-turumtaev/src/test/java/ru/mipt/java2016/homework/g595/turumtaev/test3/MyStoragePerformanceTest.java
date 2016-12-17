package ru.mipt.java2016.homework.g595.turumtaev.test3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.g595.turumtaev.task3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by galim on 18.11.2016.
 */
public class MyStoragePerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) { return new MyStorage<>(path,
            MyStringSerializationStrategy.getInstance(),
            MyStringSerializationStrategy.getInstance()); }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) { return new MyStorage<>(path,
            MyIntegerSerializationStrategy.getInstance(),
            MyDoubleSerializationStrategy.getInstance()); }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) { return new MyStorage<>(path,
            MyStudentKeySerializationStrategy.getInstance(),
            MyStudentSerializationStrategy.getInstance()); }

}

