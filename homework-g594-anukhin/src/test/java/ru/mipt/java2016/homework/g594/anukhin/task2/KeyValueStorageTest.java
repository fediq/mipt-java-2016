package ru.mipt.java2016.homework.g594.anukhin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class KeyValueStorageTest extends AbstractSingleFileStorageTest{

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KeyValueStorageImpl<String, String>(path, new StringSerializableImpl(),
                new StringSerializableImpl());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KeyValueStorageImpl<Integer, Double>(path, new IntegerSerializableImpl(),
                new DoubleSerializableImpl());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KeyValueStorageImpl<StudentKey, Student>(path, new StudentKeySerializableImpl(),
                new StudentSerializableImpl());
    }
}
