package ru.mipt.java2016.homework.g597.sigareva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by 1 on 31.10.2016.
 */
public class TestClass extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new KeyValueStorageImpl(new StringStringSerializer(path));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new KeyValueStorageImpl(new IntegerDoubleSerializer(path));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new KeyValueStorageImpl(new StudentSerializer(path));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
