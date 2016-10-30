package ru.mipt.java2016.homework.g595.popovkin.task2;

/**
 * Created by Howl on 30.10.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyStorage<String, String> res = null;
        try {
            res = new MyStorage<>(path, new StringParser(), new StringParser());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyStorage<Integer, Double> res = null;
        try {
            res = new MyStorage<>(path, new IntegerParser(), new DoubleParser());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyStorage<StudentKey, Student> res = null;
        try {
            res = new MyStorage<>(path, new StudentKeyParser(), new StudentParser());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
}
