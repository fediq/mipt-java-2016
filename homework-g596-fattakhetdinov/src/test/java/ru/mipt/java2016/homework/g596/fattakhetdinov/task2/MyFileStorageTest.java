package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class MyFileStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        KeyValueStorage<String, String> result = null;
        try {
            result = new MyKeyValueStorage<>(path,
                    new StringSerializator(),
                    new StringSerializator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        KeyValueStorage<Integer, Double> result = null;
        try {
            result = new MyKeyValueStorage<>(path,
                    new IntegerSerializator(),
                    new DoubleSerializator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        KeyValueStorage<StudentKey, Student> result = null;
        try {
            result = new MyKeyValueStorage<>(path,
                    new StudentKeySerializator(),
                    new StudentSerializator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
