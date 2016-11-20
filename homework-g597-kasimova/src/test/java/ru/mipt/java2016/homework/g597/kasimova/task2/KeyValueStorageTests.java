package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Надежда on 30.10.2016.
 */
public class KeyValueStorageTests extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MKeyValueStorage<String, String>(path, MSerialization.STRING_SERIALIZER,
                    MSerialization.STRING_SERIALIZER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MKeyValueStorage<Integer, Double>(path, MSerialization.INTEGER_SERIALIZER,
                    MSerialization.DOUBLE_SERIALIZER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MKeyValueStorage<>(path, new StudentKeySerialization(),
                    new StudentSerialization());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}