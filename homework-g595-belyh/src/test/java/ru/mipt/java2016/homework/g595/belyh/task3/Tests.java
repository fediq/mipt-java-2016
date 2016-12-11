package ru.mipt.java2016.homework.g595.belyh.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.belyh.task3.MyStundentSerializer;
import ru.mipt.java2016.homework.g595.belyh.task3.MySerializer;
import ru.mipt.java2016.homework.g595.belyh.task3.MyBackedStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public class Tests extends KeyValueStoragePerformanceTest {
    private final Map<String, Map<String, String>> stringMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Double>> numbersMaps = new ConcurrentHashMap<>();
    private final Map<String, Map<StudentKey, Student>> pojoMaps = new ConcurrentHashMap<>();

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyBackedStorage<String, String>(
                    path,
                    new MySerializer.StringSerializer(),
                    new MySerializer.StringSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyBackedStorage<Integer, Double>(
                    path,
                    new MySerializer.IntegerSerializer(),
                    new MySerializer.DoubleSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyBackedStorage<StudentKey, Student>(
                    path,
                    new MyStundentSerializer.StudentKeySerializer(),
                    new MyStundentSerializer.StudentSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }
/*
    @Test
    @Ignore
    @Override
    public void measure100kWDump100kR() {
        super.measure100kWDump100kR();
    }*/
}
