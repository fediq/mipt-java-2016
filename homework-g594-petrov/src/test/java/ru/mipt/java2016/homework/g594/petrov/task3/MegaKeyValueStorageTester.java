package ru.mipt.java2016.homework.g594.petrov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.petrov.task2.*;
import ru.mipt.java2016.homework.g594.petrov.task2.MegaKeyValueStorage;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeDouble;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeInteger;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeString;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeStudent;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeStudentKey;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by philipp on 16.11.16.
 */
public class MegaKeyValueStorageTester extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new MegaKeyValueStorage<>(path, "{String:String}", new SerializeString(), new SerializeString());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new MegaKeyValueStorage<>(path, "{Integer:Double}", new SerializeInteger(), new SerializeDouble());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new MegaKeyValueStorage<>(path, "{Student:StudenKey}", new SerializeStudentKey(), new SerializeStudent());
    }
}
