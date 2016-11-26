package ru.mipt.java2016.homework.g594.rubanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by king on 30.10.16.
 */
public class MyKeyValueStorageImplTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorageImpl(path,
                new MyStringSerializer(), new MyStringSerializer());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorageImpl(path,
                new MyIntegerSerializer(), new MyDoubleSerializer());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorageImpl(path,
                new MyStudentKeySerializer(), new MyStudentSerializer());
    }
}
