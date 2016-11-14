package ru.mipt.java2016.homework.g597.spirin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by whoami on 10/30/16.
 */
public class PersistentKeyValueStorageTest extends AbstractSingleFileStorageTest {

    private final String filename = "storage.db";

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new PersistentKeyValueStorage<>(path, filename,
                    StringSerializer.getInstance(), StringSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new PersistentKeyValueStorage<>(path, filename,
                    IntegerSerializer.getInstance(), DoubleSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new PersistentKeyValueStorage<>(path, filename,
                    StudentKeySerializer.getInstance(), StudentSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
