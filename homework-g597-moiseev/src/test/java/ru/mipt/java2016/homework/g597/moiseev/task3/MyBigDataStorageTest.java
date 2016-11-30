package ru.mipt.java2016.homework.g597.moiseev.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.moiseev.task2.DoubleSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.IntegerSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.StringSerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g597.moiseev.task2.StudentSerializationStrategy;
import ru.mipt.java2016.homework.g597.moiseev.task2.StudentKeySerializationStrategy;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;

/**
 * Тестер для дисковое хранилища.
 *
 * @author Fedor Moiseev
 * @since 21.11.16
 */
public class MyBigDataStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyBigDataStorage<String, String>(path, "storage", StringSerializationStrategy.getInstance(),
                    StringSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyBigDataStorage<Integer, Double>(path, "storage", IntegerSerializationStrategy.getInstance(), DoubleSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyBigDataStorage<StudentKey, Student>(path, "storage.db", StudentKeySerializationStrategy.getInstance(), StudentSerializationStrategy.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
