package ru.mipt.java2016.homework.g595.novikov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by igor on 10/24/16.
 */
public class MySingleFileStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage<String, String>(path,
                new StringSerialization(),
                new StringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage<Integer, Double>(path,
                new IntSerialization(),
                new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage<StudentKey, Student>(path,
                new StudentKeySerialization(),
                new StudentSerialization());
    }
}
