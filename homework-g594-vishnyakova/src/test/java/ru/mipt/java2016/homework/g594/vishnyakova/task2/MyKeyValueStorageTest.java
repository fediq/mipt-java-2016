package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Nina on 24.10.16.
 */
public class MyKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MyKeyValueStorage("String:String\n", path,
                new StringSerializationStrategy(), new StringSerializationStrategy());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MyKeyValueStorage("Integer:Double\n", path,
                new IntegerSerializationStrategy(), new DoubleSerializationStrategy());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MyKeyValueStorage("StudentKey:Student\n", path,
                new StudentKeySerializationStrategy(), new StudentSerializationStrategy());
    }
}
