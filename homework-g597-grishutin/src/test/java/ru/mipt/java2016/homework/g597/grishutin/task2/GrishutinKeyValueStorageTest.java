package ru.mipt.java2016.homework.g597.grishutin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class GrishutinKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new GrishutinKeyValueStorage<>(path,
                    new StringSerializer(),
                    new StringSerializer());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new GrishutinKeyValueStorage<>(path,
                    new IntegerSerializer(),
                    new DoubleSerializer());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new GrishutinKeyValueStorage<>(path,
                    new StudentKeySerializationStrategy(),
                    new StudentSerializationStrategy());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
