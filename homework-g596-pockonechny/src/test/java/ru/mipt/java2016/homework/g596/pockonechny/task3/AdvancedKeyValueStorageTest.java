package ru.mipt.java2016.homework.g596.pockonechny.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by celidos on 30.10.16.
 */

public class AdvancedKeyValueStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new ru.mipt.java2016.homework.g596.pockonechny.task3.AdvancedKeyValueStorage(path, new IntSerialization(),
                new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new ru.mipt.java2016.homework.g596.pockonechny.task3.AdvancedKeyValueStorage(path, new StringSerialization(),
                new StringSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new ru.mipt.java2016.homework.g596.pockonechny.task3.AdvancedKeyValueStorage(path, new StudentKeySerialization(),
                new ru.mipt.java2016.homework.g596.pockonechny.task3.StudentSerialization());
    }
}