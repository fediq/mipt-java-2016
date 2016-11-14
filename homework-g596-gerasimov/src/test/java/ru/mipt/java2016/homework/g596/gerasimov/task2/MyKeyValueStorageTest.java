package ru.mipt.java2016.homework.g596.gerasimov.task2;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.DoubleSerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.IntegerSerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StringSerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StudentKeySerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StudentSerializer;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by geras-artem on 31.10.16.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(path, new StringSerializer(),
                    new StringSerializer());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<Integer, Double>(path, new IntegerSerializer(),
                    new DoubleSerializer());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<StudentKey, Student>(path, new StudentKeySerializer(),
                    new StudentSerializer());
        } catch (IOException exception) {
            return null;
        }
    }
}
