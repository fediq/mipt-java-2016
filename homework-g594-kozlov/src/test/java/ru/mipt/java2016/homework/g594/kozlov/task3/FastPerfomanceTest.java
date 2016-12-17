package ru.mipt.java2016.homework.g594.kozlov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.kozlov.task2.StudentKeySerializer;
import ru.mipt.java2016.homework.g594.kozlov.task2.StudentSerializer;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.DoubleSerializer;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.IntegerSerializer;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.StringSerializer;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Anatoly on 18.11.2016.
 */
public class FastPerfomanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new FastStorage<>(new StringSerializer(), new StringSerializer(), path);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new FastStorage<>(new IntegerSerializer(), new DoubleSerializer(), path);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new FastStorage<>(new StudentKeySerializer(), new StudentSerializer(), path);
    }
}