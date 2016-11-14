package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by liza on 31.10.16.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(StringSerializer.getExample(),
                    StringSerializer.getExample(), path, "MyStorage.db");
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage(IntegerSerializer.getExample(),
                    DoubleSerializer.getExample(), path, "MyStorage.db");
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage(StudentKeySerializer.getExample(),
                    StudentSerializer.getExample(), path, "MyStorage.db");
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
