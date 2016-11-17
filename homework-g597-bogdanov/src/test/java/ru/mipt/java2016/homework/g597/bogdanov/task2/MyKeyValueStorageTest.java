package ru.mipt.java2016.homework.g597.bogdanov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, "storage.db",
                    StringStringSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, "storage.db", IntegerDoubleSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, "storage.db", StudentKeyStudentSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
