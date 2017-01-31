package ru.mipt.java2016.homework.g596.ivanova.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g596.ivanova.task2.DoubleSerialisation;
import ru.mipt.java2016.homework.g596.ivanova.task2.IntegerSerialisation;
import ru.mipt.java2016.homework.g596.ivanova.task2.StringSerialisation;
import ru.mipt.java2016.homework.g596.ivanova.task2.StudentKeySerialisation;
import ru.mipt.java2016.homework.g596.ivanova.task2.StudentSerialisation;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * @author julia
 * @since 20.11.16.
 */
public class BigDataStorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws
            MalformedDataException {
        try {
            return new BigDataStorage<>(path, "storage",
                    StringSerialisation.getInstance(), StringSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws
            MalformedDataException {
        try {
            return new BigDataStorage<>(path, "storage",
                    IntegerSerialisation.getInstance(), DoubleSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws
            MalformedDataException {
        try {
            return new BigDataStorage<>(path, "storage",
                    StudentKeySerialisation.getInstance(), StudentSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
