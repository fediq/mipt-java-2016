package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Ivan on 01.11.2016.
 */
public class SerializationTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(path, "myDataBase.db", new StringSerialization(),
                    new StringSerialization());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<Integer, Double>(path, "myDataBase.db", new IntegerSerialization(),
                    new DoubleSerialization());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<StudentKey, Student>(path, "myDataBase.db", new StudentKeySerialization(),
                    new StudenSerialization());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
