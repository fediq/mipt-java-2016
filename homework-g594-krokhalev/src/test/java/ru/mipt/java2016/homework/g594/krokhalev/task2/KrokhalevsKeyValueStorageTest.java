package ru.mipt.java2016.homework.g594.krokhalev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by wheeltune on 20.10.16.
 */
public class KrokhalevsKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KrokhalevsKeyValueStorage<String, String>(path);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildI2DStorage(String path) {
        return new KrokhalevsKeyValueStorage<Integer, Double>(path);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path) {
        return new KrokhalevsKeyValueStorage<StudentKey, Student>(path);
    }
}
