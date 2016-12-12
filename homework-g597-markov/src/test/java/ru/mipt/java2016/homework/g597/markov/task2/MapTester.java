package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class MapTester extends AbstractSingleFileStorageTest {

    @Override
    protected MyHashTable<String, String> buildStringsStorage(String path) {
        try {
            return new MyHashTable<>(path, "storage.db", new StringSerializator(),
                    new StringSerializator());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected MyHashTable<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyHashTable<>(path, "storage.db", new IntegerSerializator(), new DoubleSerializator());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected MyHashTable<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyHashTable<>(path, "storage.db", new StudentKeySerializator(), new StudentSerializator());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
