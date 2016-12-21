package ru.mipt.java2016.homework.g599.derut.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class DataBaseTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKVStorage(path, new StringReader(), new StringReader());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {

            return new MyKVStorage(path, new IntegerRead(), new DoubleRead());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKVStorage(path, new StudentKReader(), new StudentReader());
        } catch (IOException e) {
            return null;
        }
    }
}
