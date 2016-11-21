package ru.mipt.java2016.homework.g595.kryloff.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mipt.java2016.homework.g595.kryloff.task2.JMyDoubleSerializer;
import ru.mipt.java2016.homework.g595.kryloff.task2.JMyIntegerSerializer;
import ru.mipt.java2016.homework.g595.kryloff.task2.JMyStringSerializer;
import ru.mipt.java2016.homework.g595.kryloff.task2.JMyStudentKeySerializer;
import ru.mipt.java2016.homework.g595.kryloff.task2.JMyStudentSerializer;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public class JMyKVStorageV2Test  extends KeyValueStoragePerformanceTest{
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new JMyKVStorageV2<>(path, new JMyStringSerializer(), new JMyStringSerializer());
        } catch (IOException ex) {
            Logger.getLogger(KeyValueStoragePerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new JMyKVStorageV2<>(path, new JMyIntegerSerializer(), new JMyDoubleSerializer());
        } catch (IOException ex) {
            Logger.getLogger(KeyValueStoragePerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new JMyKVStorageV2<>(path, new JMyStudentKeySerializer(), new JMyStudentSerializer());
        } catch (IOException ex) {
            Logger.getLogger(KeyValueStoragePerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
