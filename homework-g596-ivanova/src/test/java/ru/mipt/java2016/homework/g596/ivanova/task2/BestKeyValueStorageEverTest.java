package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by julia on 30.10.16.
 */
public class BestKeyValueStorageEverTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new BestKeyValueStorageEver<String, String>(path, "storage",
                    StringSerialisation.getInstance(), StringSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new BestKeyValueStorageEver<Integer, Double>(path, "storage",
                    IntegerSerialisation.getInstance(), DoubleSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new BestKeyValueStorageEver<StudentKey, Student>(path, "storage",
                    StudentKeySerialisation.getInstance(), StudentSerialisation.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}