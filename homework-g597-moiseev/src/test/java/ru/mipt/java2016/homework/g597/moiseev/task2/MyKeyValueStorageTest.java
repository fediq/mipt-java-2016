package ru.mipt.java2016.homework.g597.moiseev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Тестер для дисковое хранилища.
 *
 * @author Fedor Moiseev
 * @since 26.10.16
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(path, "storage.db", StringSerializationStrategy.getInstance(),
                    StringSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage(path, "storage.db", IntegerSerializationStrategy.getInstance(), DoubleSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage(path, "storage.db", StudentKeySerializationStrategy.getInstance(), StudentSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
