package ru.mipt.java2016.homework.g594.nevstruev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Владислав on 31.10.2016.
 */
public class SerializeTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<String, String>(1, path, new StringSerialize(), new StringSerialize());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage<Integer, Double>(2, path, new IntSerialize(), new DoubleSerialize());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage<StudentKey, Student>(3, path, new StudentKeySerialize(), new StudentSerialize());
    }
}
