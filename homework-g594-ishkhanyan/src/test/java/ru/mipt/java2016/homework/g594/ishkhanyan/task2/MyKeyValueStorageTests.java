package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MyKeyValueStorageTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, "String", "String");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path,"Integer", "Double");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<>(path, new MyStudentKeySerialization(), new MyStudentSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

