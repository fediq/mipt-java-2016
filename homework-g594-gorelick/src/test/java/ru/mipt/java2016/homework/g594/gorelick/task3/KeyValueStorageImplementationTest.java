package ru.mipt.java2016.homework.g594.gorelick.task2;

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class KeyValueStorageImplementationTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path, new StringSerializer(),
                    new StringSerializer());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path, new IntegerSerializer(),
                    new DoubleSerializer());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new KeyValueStorageImplementation<>(path, new StudentKeySerializer(),
                    new StudentSerializer());
        } catch (IOException exception) {
            return null;
        }
    }
}
