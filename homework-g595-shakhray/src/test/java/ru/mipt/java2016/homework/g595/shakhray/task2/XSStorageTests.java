package ru.mipt.java2016.homework.g595.shakhray.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes.*;
import ru.mipt.java2016.homework.g595.shakhray.task2.Storage.XSStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Vlad on 26/10/2016.
 */
public class XSStorageTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new XSStorage(path, StringSerialization.getSerialization(), StringSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new XSStorage(path, IntegerSerialization.getSerialization(), DoubleSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new XSStorage(path, StudentKeySerialization.getSerialization(), StudentSerialization.getSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
