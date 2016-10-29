package ru.mipt.java2016.homework.g594.kozlov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.*;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class ImplKVStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KVStorageImpl<String, String>(path, new StringSerializer(),
                new StringSerializer(), "string", "string");
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KVStorageImpl<Integer, Double>(path, new IntegerSerializer(),
                new DoubleSerializer(), "integer", "double");
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KVStorageImpl<StudentKey, Student>(path, new StudentKeySerializer(),
                new StudentSerializer(), "studentkey", "student");
    }
}