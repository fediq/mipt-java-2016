package ru.mipt.java2016.homework.g595.popovkin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.popovkin.task2.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Howl on 15.11.2016.
 */

public class MyComplexStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        LackOfMemoryStorage<String, String> res = null;
        try {
            res = new LackOfMemoryStorage<>(path, new StringParserRandomAccess(), new StringParserRandomAccess());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        LackOfMemoryStorage<Integer, Double> res = null;
        try {
            res = new LackOfMemoryStorage<>(path, new IntegerParserRandomAccess(), new DoubleParserRandomAccess());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        LackOfMemoryStorage<StudentKey, Student> res = null;
        try {
            res = new LackOfMemoryStorage<>(path, new StudentKeyParserRandomAccess(), new StudentParserRandomAccess());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
}
