package ru.mipt.java2016.homework.g595.shakhray.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.DoubleSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.IntegerSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Serialization.Classes.StringSerialization;
import ru.mipt.java2016.homework.g595.shakhray.task3.Storage.PerformanceStorage;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Vlad on 26/10/2016.
 */
public class PerfomanceStorageTest extends KeyValueStoragePerformanceTest{
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new PerformanceStorage(path, StringSerialization.getSerialization(), StringSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new PerformanceStorage(path, IntegerSerialization.getSerialization(), DoubleSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new PerformanceStorage(path, StudentKeySerialization.getSerialization(), StudentSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
